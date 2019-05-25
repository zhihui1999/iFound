package com.gizwits.opensource.appkit.ControlModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.DeviceModule.GosDeviceListActivity;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.sharingdevice.messageCenterActivity;
import com.gizwits.opensource.appkit.utils.HexStrUtils;
import com.lidroid.xutils.view.annotation.event.OnTouch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GosDeviceControlActivity extends GosControlModuleBaseActivity
		implements OnClickListener, OnEditorActionListener, OnSeekBarChangeListener,LocationSource,AMapLocationListener {
	/**
	 * /////////////////////////////////////////
	 */
	//AMap是地图对象
	private static AMap aMap;
	private static MapView mapView;
	private CoordinateConverter converter;
	//地图样式变量
	public static int mapType=1;
	private boolean followMove=true;
	double latlngw;
	double latlngj;
	private int baojinglinshi=1;//1true  0false
	/**临时变量*/
	private int dingweitype=3;
	private Marker marker;
	private MarkerOptions iFound;
	//范围
	private int diyDistance=100;
	//是否报警
	private boolean baojing=true;
	//声明AMapLocationClient类对象，定位发起端
	private AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象，定位参数
	public AMapLocationClientOption mLocationOption = null;
	//声明mListener对象，定位监听器
	private LocationSource.OnLocationChangedListener mListener = null;
	//标识，用于判断是否只显示一次定位信息和用户重新定位
	private boolean isFirstLoc = true;
	private boolean isFirstMarker=true;
	//音乐
	private MediaPlayer mediaPlayer = new MediaPlayer();
	//行驶轨迹
	private Polyline polyline;
	private List<LatLng> latLngs;

	@Override
	public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
		mListener = onLocationChangedListener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	private LatLng latLng1, latLng2,desLatLng,linshi;
	private float juli;
/********************************************************/
	/**
	 *
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)
		// ，实现地图生命周期管理
		mapView.onSaveInstanceState(outState);
	}
	/**
	 * ///////////////////////////////////
	 */
	/** 设备列表传入的设备变量 */
	private GizWifiDevice mDevice;

	private enum handler_key {

		/** 更新界面 */
		UPDATE_UI,

		DISCONNECT,
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (isDeviceCanBeControlled()) {
				progressDialog.cancel();
			} else {
				toastDeviceNoReadyAndExit();
			}
		}

	};

	/** The handler. */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handler_key key = handler_key.values()[msg.what];
			switch (key) {
			case UPDATE_UI:
				updateUI();
				break;
			case DISCONNECT:
				toastDeviceDisconnectAndExit();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		initMediaPlayer();
		/**
		 * //////////////////////
		 */
		//获取地图控件引用
		mapView = (MapView) findViewById(R.id.map);
		//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
			//设置显示定位按钮 并且可以点击
			UiSettings settings = aMap.getUiSettings();
			aMap.setLocationSource(this);//设置了定位的监听
			// 是否显示定位按钮
			settings.setMyLocationButtonEnabled(true);
			aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
		}
		//历史轨迹集合
		latLngs = new ArrayList<LatLng>();
		//自定义图标
		iFound = new MarkerOptions();
		iFound.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(getResources(),R.drawable.marker)));
		//开始定位
		location();
		/**
		 * /////////////////////
		 */

		initDevice();
		setActionBar(true, true, getDeviceName());
		initEvent();
	}

	/**
	 * 音乐初始化
	 */
	private void initMediaPlayer() {
		mediaPlayer = MediaPlayer.create(this, R.raw.whistle);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		mediaPlayer.pause();
	}
	/**
	 * /////////////////////////////////
	 * 初始化定位
	 */
	private void location() {
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，
		// Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(true);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	@Override
	public void onLocationChanged(final AMapLocation aMapLocation) {
		if (aMapLocation != null) {
			if (aMapLocation.getErrorCode() == 0) {

				mListener.onLocationChanged(aMapLocation);
				// 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
				if (isFirstLoc) {
					//设置缩放级别
					aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
					//将地图移动到定位点
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
					// aMap.addMarker(getMarkerOptions(amapLocation));
					//获取定位信息
					StringBuffer buffer = new StringBuffer();
					aMap.setMyLocationStyle(new MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
					// buffer.append(aMapLocation.getLatitude());
					// Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
					isFirstLoc = false;
				}

				// 定义 Marker 点击事件监听
				AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
					// marker 对象被点击时回调的接口
					// 返回 true 则表示接口已响应事件，否则返回false
					@Override
					public boolean onMarkerClick(Marker marker) {
						if(marker.isInfoWindowShown())
						{
							marker.hideInfoWindow();
						}else
						{
							marker.showInfoWindow();
						}
						return true;
					}
				};
				// 绑定 Marker 被点击事件
				aMap.setOnMarkerClickListener(markerClickListener);


				/**
				 * 时间设置，设置每隔3s计算一次距离，若距离大于某值
				 */
//				Timer timer = new Timer();
//				timer.scheduleAtFixedRate(new TimerTask() {
//					@Override
//					public void run() {

				latLng2 = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

				//求距离
				if (desLatLng==null){

				}else {
					juli= AMapUtils.calculateLineDistance(desLatLng, latLng2);
				}


				if (juli > diyDistance && baojing && latlngw>1 && latlngj>1) {
					mediaPlayer.start();
				} else {
					mediaPlayer.pause();
				}
//					}
//				}, 1000, 3000);


			} else {
				//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError", "location Error, ErrCode:"
						+ aMapLocation.getErrorCode() + ", errInfo:"
						+ aMapLocation.getErrorInfo());
			}
		}
	}
	/**
	 * /////////////////////////////////
	 */



	private void initEvent() {


	
	}

	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(gizWifiDeviceListener);
		Log.i("Apptest", mDevice.getDid());
	}

	private String getDeviceName() {
		if (TextUtils.isEmpty(mDevice.getAlias())) {
			return mDevice.getProductName();
		}
		return mDevice.getAlias();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getStatusOfDevice();
		//在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
		mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mRunnable);
		// 退出页面，取消设备订阅
		mDevice.setSubscribe(false);
		mDevice.setListener(null);
		mediaPlayer.stop();
		aMap = null;
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	/*
	 * ========================================================================
	 * EditText 点击键盘“完成”按钮方法
	 * ========================================================================
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		switch (v.getId()) {
		default:
			break;
		}
		hideKeyBoard();
		return false;

	}
	
	/*
	 * ========================================================================
	 * seekbar 回调方法重写
	 * ========================================================================
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	/*
	 * ========================================================================
	 * 菜单栏
	 * ========================================================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.device_more, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 菜单按键
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_setDeviceInfo:
			setDeviceInfo();
			break;

		case R.id.action_getHardwareInfo:
			if (mDevice.isLAN()) {
				mDevice.getHardwareInfo();
			} else {
				myToast("只允许在局域网下获取设备硬件信息！");
			}
			break;

		case R.id.action_getStatu:
			mDevice.getDeviceStatus();
			break;

			//设置定位模式
			case R.id.action_setting:
				//判断定位频率
				if (data_gps_signal)
				{
					Toast.makeText(this, "低精度模式，低耗电", Toast.LENGTH_SHORT).show();
					sendCommand((KEY_GPS_SIGNAL),false);
//					data_gps_signal=false;
				}else {
					Toast.makeText(this, "高精度模式，高耗电", Toast.LENGTH_SHORT).show();
					sendCommand((KEY_GPS_SIGNAL),true);
//					data_gps_signal=true;
				}
				break;
			//设置地图样式
			case R.id.action_type:
				//判断当前地图样式
				if (mapType == 1) {
					mapType = 2;
				} else if (mapType == 2) {
					mapType = 3;
				} else {
					mapType = 1;
				}
				//更改样式
				switch (mapType) {
					case 1: {
						aMap.setMapType(AMap.MAP_TYPE_NORMAL);//标准地图
						break;
					}
					case 2: {
						aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置卫星地图模式，aMap是地图控制器对象。
						break;
					}
					case 3: {
						aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图，aMap是地图控制器对象。
						break;
					}
				}
				break;
			//设置定位范围，超过则报警
			case R.id.action_distance:
				final EditText et = new EditText(this);
				new AlertDialog.Builder(this).setTitle("请输入定位范围")
						.setIcon(android.R.drawable.sym_def_app_icon)
						.setView(et)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//按下确定键后的事件
								Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
								diyDistance=Integer.parseInt(et.getText().toString());
							}
						}).setNegativeButton("取消",null).show();
				break;
			case R.id.action_baojing:
				if (baojinglinshi==1) {
					baojinglinshi=0;
					baojing=false;
					Toast.makeText(this, "报警关闭，超出范围不报警", Toast.LENGTH_SHORT).show();
				}else {
					baojinglinshi=1;
					baojing=true;
					Toast.makeText(this, "报警开启，超出范围报警", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.action_iFoundbaojing:
				if (data_alarm){
					sendCommand((KEY_ALARM),false);
					Toast.makeText(this, "已关闭报警，再次点击开启报警", Toast.LENGTH_SHORT).show();
				}else {
					sendCommand((KEY_ALARM),true);
					Toast.makeText(this, "报警中，再次点击关闭报警", Toast.LENGTH_SHORT).show();
				}
				break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Description:根据保存的的数据点的值来更新UI
	 */
	protected void updateUI() {
		// 添加图钉，定位点
		latlngw=StrDou(HexStrUtils.splitBytesString(HexStrUtils.bytesToHexString(data_latitude)).replaceAll(" ", ""),2);
		latlngj=StrDou(HexStrUtils.splitBytesString(HexStrUtils.bytesToHexString(data_longitude)).replaceAll(" ", ""),3);
		latLng1 = new LatLng(latlngw, latlngj);


		if (!(linshi==latLng1)){
			latLngs.add(latLng1);
			polyline =aMap.addPolyline(new PolylineOptions().
					addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
		}
		linshi=latLng1;
		if (marker==null){
			marker=aMap.addMarker(iFound.position(latLng1).title("iFound定位器").snippet("你的定位器"));
		}else {
			marker.remove();
			converter  = new CoordinateConverter(this);
			converter.from(CoordinateConverter.CoordType.GPS);
			converter.coord(latLng1);
			desLatLng = converter.convert();
			marker=aMap.addMarker(iFound.position(desLatLng));
		}



	}

	private void setEditText(EditText et, Object value) {
		et.setText(value.toString());
		et.setSelection(value.toString().length());
		et.clearFocus();
	}

	/**
	 * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
	 */
	private void getStatusOfDevice() {
		// 设备是否可控
		if (isDeviceCanBeControlled()) {
			// 可控则查询当前设备状态
			mDevice.getDeviceStatus();
		} else {
			// 显示等待栏
			progressDialog.show();
			if (mDevice.isLAN()) {
				// 小循环10s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 10000);
			} else {
				// 大循环20s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 20000);
			}
		}
	}

	/**
	 * 发送指令,下发单个数据点的命令可以用这个方法
	 * 
	 * <h3>注意</h3>
	 * <p>
	 * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
	 * </p>
	 * 
	 * @param key
	 *            数据点对应的标识名
	 * @param value
	 *            需要改变的值
	 */
	private void sendCommand(String key, Object value) {
		if (value == null) {
			return;
		}
		int sn = 5;
		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
		hashMap.put(key, value);
		// 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
		// hashMap.put(key2, value2);
		// hashMap.put(key3, value3);
		mDevice.write(hashMap, sn);
		Log.i("liang", "下发命令：" + hashMap.toString());
	}

	private boolean isDeviceCanBeControlled() {
		return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
	}

	private void toastDeviceNoReadyAndExit() {
		Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
		finish();
	}

	private void toastDeviceDisconnectAndExit() {
		Toast.makeText(GosDeviceControlActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * 展示设备硬件信息
	 * 
	 * @param hardwareInfo
	 */
	private void showHardwareInfo(String hardwareInfo) {
		String hardwareInfoTitle = "设备硬件信息";
		new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
				.setPositiveButton(R.string.besure, null).show();
	}

	/**
	 * Description:设置设备别名与备注
	 */
	private void setDeviceInfo() {

		final Dialog mDialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
		mDialog.show();

		Window window = mDialog.getWindow();
		window.setContentView(R.layout.alert_gos_set_device_info);

		final EditText etAlias;
		final EditText etRemark;
		etAlias = (EditText) window.findViewById(R.id.etAlias);
		etRemark = (EditText) window.findViewById(R.id.etRemark);

		LinearLayout llNo, llSure;
		llNo = (LinearLayout) window.findViewById(R.id.llNo);
		llSure = (LinearLayout) window.findViewById(R.id.llSure);

		if (!TextUtils.isEmpty(mDevice.getAlias())) {
			setEditText(etAlias, mDevice.getAlias());
		}
		if (!TextUtils.isEmpty(mDevice.getRemark())) {
			setEditText(etRemark, mDevice.getRemark());
		}

		llNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		llSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etRemark.getText().toString())
						&& TextUtils.isEmpty(etAlias.getText().toString())) {
					myToast("请输入设备别名或备注！");
					return;
				}
				mDevice.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
				mDialog.dismiss();
				String loadingText = (String) getText(R.string.loadingtext);
				progressDialog.setMessage(loadingText);
				progressDialog.show();
			}
		});

		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				hideKeyBoard();
			}
		});
	}
	
	/*
	 * 获取设备硬件信息回调
	 */
	@Override
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, String> hardwareInfo) {
		super.didGetHardwareInfo(result, device, hardwareInfo);
		StringBuffer sb = new StringBuffer();
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
			myToast("获取设备硬件信息失败：" + result.name());
		} else {
			sb.append("Wifi Hardware Version:" + hardwareInfo.get(WIFI_HARDVER_KEY) + "\r\n");
			sb.append("Wifi Software Version:" + hardwareInfo.get(WIFI_SOFTVER_KEY) + "\r\n");
			sb.append("MCU Hardware Version:" + hardwareInfo.get(MCU_HARDVER_KEY) + "\r\n");
			sb.append("MCU Software Version:" + hardwareInfo.get(MCU_SOFTVER_KEY) + "\r\n");
			sb.append("Wifi Firmware Id:" + hardwareInfo.get(WIFI_FIRMWAREID_KEY) + "\r\n");
			sb.append("Wifi Firmware Version:" + hardwareInfo.get(WIFI_FIRMWAREVER_KEY) + "\r\n");
			sb.append("Product Key:" + "\r\n" + hardwareInfo.get(PRODUCT_KEY) + "\r\n");

			// 设备属性
			sb.append("Device ID:" + "\r\n" + mDevice.getDid() + "\r\n");
			sb.append("Device IP:" + mDevice.getIPAddress() + "\r\n");
			sb.append("Device MAC:" + mDevice.getMacAddress() + "\r\n");
		}
		showHardwareInfo(sb.toString());
	}
	
	/*
	 * 设置设备别名和备注回调
	 */
	@Override
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
		super.didSetCustomInfo(result, device);
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
			myToast("设置成功");
			progressDialog.cancel();
			finish();
		} else {
			myToast("设置失败：" + result.name());
		}
	}

	/*
	 * 设备状态改变回调，只有设备状态为可控才可以下发控制命令
	 */
	@Override
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
		super.didUpdateNetStatus(device, netStatus);
		if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
			mHandler.removeCallbacks(mRunnable);
			progressDialog.cancel();
		} else {
			mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
		}
	}
	
	/*
	 * 设备上报数据回调，此回调包括设备主动上报数据、下发控制命令成功后设备返回ACK
	 */
	@Override
	protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, Object> dataMap, int sn) {
		super.didReceiveData(result, device, dataMap, sn);
		Log.i("liang", "接收到数据");
		if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
			getDataFromReceiveDataMap(dataMap);
			mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
		}
	}


	//
	private double StrDou(String date,int quzhi){
        // TODO Auto-generated method stub
        String a=date;//
        char curr = 0;
        int c = a.indexOf("2E");

        String num="";
        for(int i=0;i<a.length();i++) {
            if(i==c) {
                num+=".";
                i=i+2;
            }
            if(i%2!=0) {
                curr = a.charAt(i);
                num+=curr;
            }
        }
        num.replace('e','0');
        num.replace('E','0');
		double doua = Double.parseDouble(num.substring(0, quzhi));
		double doub = Double.parseDouble(num.substring(quzhi));
		doua=doua+doub/60;

//        System.out.println(num);
//        System.out.println(Double.parseDouble(num));

//		Toast.makeText(this, String.valueOf(doua), Toast.LENGTH_SHORT).show();
        return doua;
    }

//	@Override
//	protected void onPause() {
//		super.onPause();
//		//在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
//		mapView.onPause();
//	}

}
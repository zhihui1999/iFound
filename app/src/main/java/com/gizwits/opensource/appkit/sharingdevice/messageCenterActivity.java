package com.gizwits.opensource.appkit.sharingdevice;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizMessage;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizMessageType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSharingListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.ControlModule.GosControlModuleBaseActivity;
import com.gizwits.opensource.appkit.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class messageCenterActivity extends GosControlModuleBaseActivity implements LocationSource,AMapLocationListener {
	private LinearLayout gizwitsmes;
	/**
	 * /////////////////////////////////////////
	 */
	//AMap是地图对象
	private static AMap aMap;
	private static MapView mapView;
	//地图样式变量
	public static int mapType=1;
	//范围
	public static int diyDistance=100;
	//是否报警
	public static boolean baojing=true;
	//声明AMapLocationClient类对象，定位发起端
	private AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象，定位参数
	public AMapLocationClientOption mLocationOption = null;
	//声明mListener对象，定位监听器
	private LocationSource.OnLocationChangedListener mListener = null;
	//标识，用于判断是否只显示一次定位信息和用户重新定位
	private boolean isFirstLoc = true;
	//音乐
	private MediaPlayer mediaPlayer = new MediaPlayer();

	@Override
	public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
		mListener = onLocationChangedListener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	private LatLng latLng1, latLng2;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gos_message);

		//初始化MediaPlayer
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
		//开始定位
		location();
		/**
		 * /////////////////////
		 */

		initView();
	}

	/**
	 * 音乐初始化
	 */
	private void initMediaPlayer() {
		mediaPlayer = MediaPlayer.create(this, R.raw.whistle);
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

					// buffer.append(aMapLocation.getLatitude());
					// Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
					isFirstLoc = false;
				}


				// 添加图钉，定位点
				latLng1 = new LatLng(38.284476728207799, 116.78400366573392);

				MarkerOptions iFound = new MarkerOptions();
				iFound.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),R.drawable.marker)));

				Marker marker = aMap.addMarker(iFound.position(latLng1).title("定位器").snippet("你的某个定位器"));

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
//						latLng2 = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//						//求距离
//						juli=AMapUtils.calculateLineDistance(latLng1, latLng2);
//						if (juli > diyDistance&&baojing) {
//							mediaPlayer.start();
//						} else {
//							mediaPlayer.pause();
//						}
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

	@Override
	public void onResume() {
		super.onResume();
		String token = spf.getString("Token", "");
		GizDeviceSharing.queryMessageList(token, GizMessageType.GizMessageSharing);
		//GizDeviceSharing.queryMessageList(token, GizMessageType.GizMessageSystem);

		GizDeviceSharing.setListener(new GizDeviceSharingListener() {

			@Override
			public void didQueryMessageList(GizWifiErrorCode result, List<GizMessage> messageList) {
				super.didQueryMessageList(result, messageList);

				if (result.ordinal() != 0) {
					Toast.makeText(messageCenterActivity.this, toastError(result), 2).show();
				}

			}

		});
	}



	private void initView() {
		gizwitsmes = (LinearLayout) findViewById(R.id.gizwitsmes);
	}



	// 跳转到机智云公告页面
	public void gizwitsmes(View v) {
		
		Intent intent = new Intent(this, MsgNoticeActivity.class);
		startActivity(intent);
		
		gizwitsmes.setEnabled(false);
		gizwitsmes.postDelayed(new Runnable() {
			@Override
			public void run() {
				gizwitsmes.setEnabled(true);
			}
		}, 1000);
	}

	public static void updateMap(){
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mapView.onDestroy();
		mediaPlayer.stop();
		mLocationClient.stopLocation();//停止定位
		mLocationClient.onDestroy();//销毁定位客户端。
	}
}

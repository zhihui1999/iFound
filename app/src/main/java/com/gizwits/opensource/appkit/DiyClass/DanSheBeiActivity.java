package com.gizwits.opensource.appkit.DiyClass;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.gizwits.opensource.appkit.ControlModule.GosControlModuleBaseActivity;
import com.gizwits.opensource.appkit.R;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class DanSheBeiActivity extends GosControlModuleBaseActivity implements AdapterView.OnItemClickListener {
    private List<DanSheBei> mData = null;
    private Context mContext;
    private DanSheBeiAdapter mAdapter = null;
    private ListView list_danshebei;
    private String data;
    private TextView dl;//电量

    private static final UUID MY_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID write_UUID=UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID notify_UUID=UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private BluetoothAdapter bAdapter;
    public Set<BluetoothDevice> peidui;
    private String MAC="80:7D:3A:A2:EC:22";
    private BleDevice ableDevice;//选出来的蓝牙模块
    private boolean isbaojing=false,issuccess=false;//是否在报警，是否连接成功
    private String baojingchar="A";
    static final String[] PERMISSION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,//位置
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
            Manifest.permission.WRITE_CALL_LOG,        //读取设备信息
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dan_she_bei);
        mContext = getApplication();

        //消除Bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        //接受数据
        Intent intent = getIntent();
        data = intent.getStringExtra("DanSheBei");

        setPermissions();//动态权限申请
        //初始化
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
        BleManager.getInstance().enableBluetooth();

        bAdapter= BluetoothAdapter.getDefaultAdapter();
        peidui = bAdapter.getBondedDevices();
        if (peidui.size()>0){
            for(Iterator<BluetoothDevice> iterator = peidui.iterator(); iterator.hasNext();){
                BluetoothDevice bluetoothDevice=(BluetoothDevice)iterator.next();
                if (BleManager.getInstance().convertBleDevice(bluetoothDevice).getMac()==MAC){
                    ableDevice = BleManager.getInstance().convertBleDevice(bluetoothDevice);
                }
            }
        }


        Button button = (Button) findViewById(R.id.DanSheBei_shanchu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
            }
        });

        list_danshebei = (ListView) findViewById(R.id.DanSheBeilist);
        mData = new LinkedList<DanSheBei>();
//添加信息
        mData.add(new DanSheBei("设备名称", data));
        mData.add(new DanSheBei("成员管理", ""));
        if (data_gps_signal){
            mData.add(new DanSheBei("定位模式", "低频率"));
        }else{
            mData.add(new DanSheBei("定位模式", "高频率"));
        }

        mData.add(new DanSheBei("休眠时段", "开启"));
        mData.add(new DanSheBei("定位器关机", ""));
        mAdapter = new DanSheBeiAdapter((LinkedList<DanSheBei>) mData, mContext);
        list_danshebei.setAdapter(mAdapter);
        list_danshebei.setOnItemClickListener(this);
//电量信息
        Intent level = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int dianliang = level.getIntExtra("level", 0);// 获得当前电量
        dl = (TextView) findViewById(R.id.dl);
        dl.setText(dianliang + "%");


        //获取系统的日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//年
        int month = calendar.get(Calendar.MONTH) + 1;//月 月份从0开始的
        int day = calendar.get(Calendar.DAY_OF_MONTH);//日
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//小时
        int minute = calendar.get(Calendar.MINUTE);//分钟
        int second = calendar.get(Calendar.SECOND);//秒

        TextView time = (TextView) findViewById(R.id.danshebei_time);
        time.setText(month+"月"+day+"日"+hour+"点"+minute+"分");

        Button shanchu = (Button) findViewById(R.id.DanSheBei_shanchu);
        shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
            }
        });
        Button baojing = (Button) findViewById(R.id.DanSheBei_baojing);
        baojing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不是连接状态则连接蓝牙模块
                if (!BleManager.getInstance().isConnected(MAC)){
                    BleManager.getInstance().connect(MAC, new BleGattCallback() {
                        @Override
                        public void onStartConnect() {
                            Log.d("蓝牙", "onStartConnect: ");
                        }

                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {
                            Log.d("蓝牙", "onConnectFail: ");
                            issuccess=false;
                        }

                        @Override
                        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            Log.d("蓝牙", "onConnectSuccess: ");
                            ableDevice=bleDevice;
                            issuccess=true;
                            Toast.makeText(mContext, "连接成功再次点击报警即可报警", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            Log.d("蓝牙", "onDisConnected: ");
                            issuccess=false;
                        }
                    });
                }

                if (issuccess){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isbaojing){
                                baojingchar="B";
                                isbaojing=false;
                            }else {
                                baojingchar="A";
                                isbaojing=true;
                            }
                            BleManager.getInstance().write(ableDevice, MY_UUID.toString(), write_UUID.toString(), baojingchar.getBytes(), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                            Log.d("蓝牙", "onWriteSuccess: 蓝牙写成功");
                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {
                                            Log.d("蓝牙", "onWriteSuccess: 蓝牙写失败"+exception.toString());
                                        }
                                    });
                        }
                    }).start();
                }

//                BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
//                    @Override
//                    public void onStartConnect() {
//                        Log.d("蓝牙", "onStartConnect: 开始进行连接。");
//                    }
//
//                    @Override
//                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
//                        Log.d("蓝牙", "onStartConnect: 连接不成功。"+exception.toString());
//                    }
//
//                    @Override
//                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                        Log.d("蓝牙", "onStartConnect: 连接成功并发现服务。。");
//                        List<BluetoothGattService> serviceList = gatt.getServices();
//                        for (BluetoothGattService service : serviceList) {
//                            UUID uuid_service = service.getUuid();
//
//                            List<BluetoothGattCharacteristic> characteristicList= service.getCharacteristics();
//                            for(BluetoothGattCharacteristic characteristic : characteristicList) {
//                                UUID uuid_chara = characteristic.getUuid();
//                                Log.d("蓝牙", "onConnectSuccess: "+uuid_chara);
//                            }
//                            Log.d("蓝牙", "onConnectSuccess: "+uuid_service.toString());
//                        }
//                        new Thread(new Runnable() {
//                            public void run() {
//                                //sleep设置的是时长
//                                try {
//                                    Thread.sleep(1000);
//                                    BleManager.getInstance().write(bleDevice, MY_UUID.toString(), write_UUID.toString(), "A".getBytes(), new BleWriteCallback() {
//                                        @Override
//                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                            Log.d("蓝牙", "onWriteSuccess: 蓝牙写成功");
//                                        }
//
//                                        @Override
//                                        public void onWriteFailure(BleException exception) {
//                                            Log.d("蓝牙", "onWriteSuccess: 蓝牙写失败"+exception.toString());
//                                        }
//                                    });
//
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }).start();
//
//                    }
//
//                    @Override
//                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
//                        Log.d("蓝牙", "onStartConnect: 连接断开。");
//                    }
//                });
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Toast.makeText(mContext, "更改设备名称", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Intent intent = new Intent(DanSheBeiActivity.this, ChengYuanActivity.class);
                intent.putExtra("chengyuan", data);
                startActivity(intent);
                break;
            case 2:
                if(data_gps_signal)
                {

                    mData.get(2).setaJieShi("低频率");
                    mAdapter = new DanSheBeiAdapter((LinkedList<DanSheBei>) mData, mContext);
                    list_danshebei.setAdapter(mAdapter);
                    data_gps_signal=false;

                }else{
                    mData.get(2).setaJieShi("高频率");
                    mAdapter = new DanSheBeiAdapter((LinkedList<DanSheBei>) mData, mContext);
                    list_danshebei.setAdapter(mAdapter);
                    data_gps_signal=true;
                }
                break;
            case 3:
                Toast.makeText(mContext, "更改休眠时段", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(mContext, "定位器关机", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    /**
     * 设置Android6.0的权限申请
     */
    private void setPermissions() {

        if (ContextCompat.checkSelfPermission(DanSheBeiActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(this,PERMISSION,1);
        }else{
            Log.i("蓝牙","权限申请ok");
        }
    }


}

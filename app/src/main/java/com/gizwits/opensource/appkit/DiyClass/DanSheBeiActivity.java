package com.gizwits.opensource.appkit.DiyClass;

import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.opensource.appkit.ControlModule.GosControlModuleBaseActivity;
import com.gizwits.opensource.appkit.R;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DanSheBeiActivity extends GosControlModuleBaseActivity implements AdapterView.OnItemClickListener {
    private List<DanSheBei> mData = null;
    private Context mContext;
    private DanSheBeiAdapter mAdapter = null;
    private ListView list_danshebei;
    private String data;
    private TextView dl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dan_she_bei);
        //消除Bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        //接受数据
        Intent intent = getIntent();
        data = intent.getStringExtra("DanSheBei");

        //
        Button button = (Button) findViewById(R.id.DanSheBei_shanchu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
            }
        });
        mContext = getApplication();
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
//年
        int year = calendar.get(Calendar.YEAR);
//月 月份从0开始的
        int month = calendar.get(Calendar.MONTH) + 1;
//日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
//小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//分钟
        int minute = calendar.get(Calendar.MINUTE);
//秒
        int second = calendar.get(Calendar.SECOND);
        TextView time = (TextView) findViewById(R.id.danshebei_time);
        time.setText(month+"月"+day+"日"+hour+"点"+minute+"分");

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
}

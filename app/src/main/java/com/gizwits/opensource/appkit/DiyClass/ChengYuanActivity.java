package com.gizwits.opensource.appkit.DiyClass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gizwits.opensource.appkit.R;
import java.util.LinkedList;
import java.util.List;

public class ChengYuanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<ChengYuan> mData = null;
    private Context mContext;
    private ChengYuanAdapter mAdapter = null;
    private ListView list_chengyuan;
    private MyDBOpenHelper myDBHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheng_yuan);

        //消除Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        mContext = getApplication();
        myDBHelper = new MyDBOpenHelper(mContext,"my.db",null,1);//数据库
        db=myDBHelper.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        Cursor cursor = db.query("person", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int pid = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                sb.append("id：" + pid + "：" + name + "\n"+phone);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Toast.makeText(mContext, sb.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(mContext, "创建数据库了", Toast.LENGTH_SHORT).show();
        list_chengyuan = (ListView) findViewById(R.id.ChengYuanlist);
        mData = new LinkedList<ChengYuan>();
//添加信息
        mData.add(new ChengYuan("赵志辉", "177****8115",R.drawable.cy1,R.drawable.xiugai));
        mData.add(new ChengYuan("张志遥", "177****8116",R.drawable.cy2,R.drawable.xiugai));
        mData.add(new ChengYuan("李昂", "177****8117",R.drawable.cy3,R.drawable.xiugai));
        mData.add(new ChengYuan("魏东珣", "177****8118",R.drawable.cy4,R.drawable.xiugai));
        mData.add(new ChengYuan("刘佳", "177****8117",R.drawable.cy5,R.drawable.xiugai));
        mData.add(new ChengYuan("","",R.drawable.cy5,R.drawable.xiugai));
        mAdapter = new ChengYuanAdapter((LinkedList<ChengYuan>) mData, mContext);
        list_chengyuan.setAdapter(mAdapter);
        list_chengyuan.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "你点击了第"+ (position+1) +"项", Toast.LENGTH_SHORT).show();
    }

}
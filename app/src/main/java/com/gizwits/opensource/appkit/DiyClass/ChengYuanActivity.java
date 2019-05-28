package com.gizwits.opensource.appkit.DiyClass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.opensource.appkit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class ChengYuanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<ChengYuan> mData = null;
    private Context mContext;
    private ChengYuanAdapter mAdapter = null;
    private ListView list_chengyuan;
    private String path = "http://www.520mylove.cn/chengyuan/select.php";
    private JSONObject jsonObject;
    private Handler handler=null;
    private String TAG = "网络";
    private boolean kedian=false;//添加是否可以点



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheng_yuan);

        //消除Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mContext = getApplication();
        handler=new Handler();
        list_chengyuan = (ListView) findViewById(R.id.ChengYuanlist);
        mData = new LinkedList<ChengYuan>();
        //获取数据库成员json数据,返回的jsonobject不为null则为获取成功
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetWebInfo();
            }
        }).start();

        TextView addchengyuan = (TextView) findViewById(R.id.addchengyuan);
        addchengyuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kedian){
                    Intent intent = new Intent(ChengYuanActivity.this, DataBaseActivity.class);
                    intent.putExtra("id", jsonObject.length()+1);//id
                    intent.putExtra("type",1);//type 0 修改  1 增加
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext, "请稍等再试", Toast.LENGTH_SHORT).show();   
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "你点击了第" + (position + 1) + "项", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ChengYuanActivity.this, DataBaseActivity.class);
        intent.putExtra("id", position+1);//id
        intent.putExtra("type",0);//type 0 修改  1 增加
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 添加数据刷新UI
     */
    void UpdataUI(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null || jsonObject.length() == 0) {
            Log.d(TAG, "UpdataUI: jsonObject长度为0或者jsonObject=null");
        } else {
            Log.d(TAG, "UpdataUI: "+jsonObject.length());
            for (int i = 0; jsonObject.length() > i; i++) {
                String one = jsonObject.getString(String.valueOf(i));//取出每个成员信息
                JSONObject onejson = new JSONObject(one);
                mData.add(new ChengYuan(onejson.getString("name"), onejson.getString("phone"), R.drawable.cy1, R.drawable.xiugai));
                Log.d(TAG, "UpdataUI: 1");
            }
            // 构建Runnable对象，在runnable中更新界面
            Runnable  runnableUi=new  Runnable(){
                @Override
                public void run() {
                    //更新界面
                    mAdapter = new ChengYuanAdapter((LinkedList<ChengYuan>) mData, mContext);
                    list_chengyuan.setAdapter(mAdapter);
                    list_chengyuan.setOnItemClickListener(ChengYuanActivity.this);
                }
            };
            handler.post(runnableUi);
            kedian=true;
            //添加信息
//            mData.add(new ChengYuan("赵志辉", "177****8115", R.drawable.cy1, R.drawable.xiugai));
//            mData.add(new ChengYuan("张志遥", "177****8116", R.drawable.cy2, R.drawable.xiugai));
//            mData.add(new ChengYuan("李昂", "177****8117", R.drawable.cy3, R.drawable.xiugai));
//            mData.add(new ChengYuan("魏东珣", "177****8118", R.drawable.cy4, R.drawable.xiugai));
//            mData.add(new ChengYuan("刘佳", "177****8117", R.drawable.cy5, R.drawable.xiugai));

        }

    }

    void GetWebInfo(){
        try {
            String htmlContent = HtmlService.getHtml(path);
            String xin = htmlContent.replace("\\/", "/");
            Log.d(TAG, "run: " + xin);
            jsonObject = new JSONObject(xin);
            UpdataUI(jsonObject);
        } catch (Exception e) {
            jsonObject = null;
            Log.d(TAG, "run:程序出现异常：" + e.toString());
        }
    }


}



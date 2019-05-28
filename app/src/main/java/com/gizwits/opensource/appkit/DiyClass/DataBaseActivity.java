package com.gizwits.opensource.appkit.DiyClass;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gizwits.opensource.appkit.R;

import java.util.HashMap;
import java.util.Map;

public class DataBaseActivity extends AppCompatActivity {

    private EditText name, phone;
    private Button delete, sure;
    private int id, type;//id 数据库id  type 0 修改  1 增加
    private String TAG ="数据库";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        id = intent.getIntExtra("id", 0);
        Log.d(TAG, "onCreate: id="+id+"\ttype="+type);

        name = (EditText) findViewById(R.id.edit_name);
        phone = (EditText) findViewById(R.id.edit_phone);
        if (type == 1) {
            delete = (Button) findViewById(R.id.bt_dellete);
            delete.setVisibility(View.INVISIBLE);
        }else {
            delete = (Button) findViewById(R.id.bt_dellete);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", String.valueOf(id));
                            Log.d(TAG, "onClick: 没事");
                            HttpUtils.sendPostMessage(params,"get",1);
                        }
                    }).start();
                    Log.d(TAG, "onClick: 删除按钮");
                }
            });
        }

        sure = (Button) findViewById(R.id.bt_sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()){
                    Log.d(TAG, "onClick: 确定按钮");
                    if (type==1){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", String.valueOf(id));
                                params.put("name", name.getText().toString());
                                params.put("phone", phone.getText().toString());
                                params.put("image", "http:////suibian");
                                Log.d(TAG, "onClick: 没事");
                                HttpUtils.sendPostMessage(params,"utf-8",0);
                            }
                        }).start();
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", String.valueOf(id));
                                params.put("name", name.getText().toString());
                                params.put("phone", phone.getText().toString());
                                params.put("image", "http:////suibian");
                                HttpUtils.sendPostMessage(params,"utf-8",2);
                            }
                        }).start();
                    }
                }
            }
        });
    }

    /**
     * 检查控件中内容是否违规
     * @return true 无违规
     */
    boolean check(){
        //检查姓名，空，过长
        if (name.getText().toString().equals("")){
            Toast.makeText(this, "没有填写名字", Toast.LENGTH_SHORT).show();
            return false;
        }else if (name.getText().length()>5){
            Toast.makeText(this, "名字过长", Toast.LENGTH_SHORT).show();
            return false;
        }
        //检查手机号
        if (phone.getText().toString().equals("")){
            Toast.makeText(this, "手机号为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

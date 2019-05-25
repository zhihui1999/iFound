package com.gizwits.opensource.appkit.DiyClass;

import android.app.Fragment;
import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.gizwits.opensource.appkit.R;

import java.util.LinkedList;
import java.util.List;


public class Tab1Fragment extends android.support.v4.app.Fragment {
    private ImageButton imagefabu;
    private ImageButton imagewode;

    private List<People> mData = null;
    private Context mContext;
    private PeopleAdapter mAdapter = null;
    private ListView list_animal;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, null);
        imagefabu = (ImageButton) view.findViewById(R.id.imagefabu);
        imagefabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击发送触发的过程（方法）
                Toast.makeText(mContext, "发布寻人", Toast.LENGTH_SHORT).show();
            }
        });
        imagewode = (ImageButton) view.findViewById(R.id.imagewode);
        imagewode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击我的触发的过程（方法）
                Toast.makeText(mContext, "我的发布", Toast.LENGTH_SHORT).show();
            }
        });

        mContext = getActivity();
        list_animal = (ListView) view.findViewById(R.id.list_view);
        mData = new LinkedList<People>();
//添加信息
        mData.add(new People("某某某", "身穿粉色棉袄，瓜子脸，尖鼻梁，樱桃小嘴\n详细简介" +
                "\n\t2018.5.4\t\t未结案", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介：男\n" +
                "内容简介\n2018.5.4\t沧州", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.5.4\t沧州", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.5.4\t沧州", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.5.4\t沧州", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.6.01\t未结案", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.6.01\t未结案", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.6.01\t未结案", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.6.01\t未结案", R.drawable.nanren));
        mData.add(new People("某某某", "内容简介\n" +
                "内容简介\n2018.5.30\t 结案", R.drawable.nanren));
        mAdapter = new PeopleAdapter((LinkedList<People>) mData, mContext);
        list_animal.setAdapter(mAdapter);
        return view;
    }
}

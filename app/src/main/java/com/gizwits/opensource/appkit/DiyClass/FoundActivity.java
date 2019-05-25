package com.gizwits.opensource.appkit.DiyClass;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.gizwits.opensource.appkit.R;

import java.util.ArrayList;
import java.util.List;


public class FoundActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {"寻人", "宠物", "健康", "教育", "生活"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        //沉浸模式代码
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //实例化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //加载界面的前后个数，详情见iGrid文件夹“关于viewpage的前后加载问题”
        //   viewPager.setOffscreenPageLimit(3);
        //页面，数据源
        list = new ArrayList<>();
        list.add(new Tab1Fragment());//寻人
        list.add(new Tab2Fragment());//宠物
        list.add(new Tab3Fragment());//健康
        list.add(new Tab4Fragment());//教育
        list.add(new Tab5Fragment());//生活
        //ViewPager的适配器
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //绑定
        tabLayout.setupWithViewPager(viewPager);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        //重写这个方法，将设置每个Tab的标题·
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override

    protected void onStart() {

        super.onStart();


    }

    @Override

    protected void onResume() {

        super.onResume();



    }

    @Override

    protected void onPause() {

        super.onPause();


    }

    @Override

    protected void onStop() {

        super.onStop();



    }

    @Override

    protected void onDestroy() {

        super.onDestroy();



    }

    @Override

    protected void onRestart() {

        super.onRestart();



    }
}

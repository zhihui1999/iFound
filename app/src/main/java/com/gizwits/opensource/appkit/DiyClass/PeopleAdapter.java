package com.gizwits.opensource.appkit.DiyClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gizwits.opensource.appkit.R;

import java.util.LinkedList;

public class PeopleAdapter extends BaseAdapter {
    private LinkedList<People> mData;
    private Context mContext;

    public PeopleAdapter(LinkedList<People> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_people,parent,false);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.txt_aName);
        TextView txt_aSpeak = (TextView) convertView.findViewById(R.id.txt_aSpeak);
        img_icon.setBackgroundResource(mData.get(position).getaIcon());
        txt_aName.setText(mData.get(position).getaName());
        txt_aSpeak.setText(mData.get(position).getaSpeak());
        return convertView;
    }
}

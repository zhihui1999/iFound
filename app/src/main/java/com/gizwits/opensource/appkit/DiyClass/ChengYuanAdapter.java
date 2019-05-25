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

public class ChengYuanAdapter extends BaseAdapter {
    private LinkedList<ChengYuan> mData;
    private Context mContext;

    public ChengYuanAdapter(LinkedList<ChengYuan> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_chengyuan,parent,false);
        TextView txt_aname = (TextView) convertView.findViewById(R.id.ChengYuan_name);
        TextView txt_aphone = (TextView) convertView.findViewById(R.id.ChengYuan_phone);
        ImageView icon = (ImageView) convertView.findViewById(R.id.ChengYuan_icon);
        ImageView edit = (ImageView) convertView.findViewById(R.id.ChengYuan_edit);
        txt_aname.setText(mData.get(position).getaName());
        txt_aphone.setText(mData.get(position).getaPhone());
        icon.setBackgroundResource(mData.get(position).getaIcon());
        edit.setBackgroundResource(mData.get(position).getaEdit());
        return convertView;
    }
}
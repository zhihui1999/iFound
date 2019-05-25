package com.gizwits.opensource.appkit.DiyClass;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gizwits.opensource.appkit.R;

import java.util.LinkedList;

public class DanSheBeiAdapter extends BaseAdapter {
    private LinkedList<DanSheBei> mData;
    private Context mContext;

    public DanSheBeiAdapter(LinkedList<DanSheBei> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_danshebei,parent,false);
        TextView txt_acaidan = (TextView) convertView.findViewById(R.id.danshebei_caidan);
        TextView txt_ajieshi = (TextView) convertView.findViewById(R.id.danshebei_jieshi);
        txt_acaidan.setText(mData.get(position).getaCaiDan());
        txt_ajieshi.setText(mData.get(position).getaJieShi());
        return convertView;
    }
}
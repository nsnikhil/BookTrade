package com.trade.book.booktrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nikhil on 19-Mar-17.
 */

public class CateogoryAdapter extends BaseAdapter{

    Context mContext;
    ArrayList<String> list;

    CateogoryAdapter(Context context, ArrayList<String> name){
        mContext = context;
        list = name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cat_item,parent,false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        myViewHolder.catName.setText(list.get(position));
        return convertView;
    }

    public class MyViewHolder{
        TextView catName;
        MyViewHolder(View v){
            catName = (TextView)v.findViewById(R.id.catName);
        }
    }
}

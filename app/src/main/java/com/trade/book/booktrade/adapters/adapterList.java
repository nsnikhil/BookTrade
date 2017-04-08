package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.trade.book.booktrade.R;
import java.util.ArrayList;



public class adapterList extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> list;

    public adapterList(Context context, ArrayList<String> name){
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

    private class MyViewHolder{
        TextView catName;
        MyViewHolder(View v){
            catName = (TextView)v.findViewById(R.id.catName);
        }
    }
}

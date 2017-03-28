package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.book.booktrade.R;
import com.trade.book.booktrade.objects.RequestObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nikhil on 28-Mar-17.
 */

public class adapterRequest extends BaseAdapter{

    Context mContext;
    ArrayList<RequestObject> mList;
    Random r = new Random();
    private static final String[] colorArray = {"#D32F2F","#C2185B","#7B1FA2","#512DA8","#303F9F","#1976D2","#0288D1",
            "#0097A7","#00796B","#388E3C","#689F38","#AFB42B","#FBC02D","#FFA000","#F57C00","#E64A19"};

    public adapterRequest(Context c, ArrayList<RequestObject> list){
        mContext = c;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.request_item_layout,parent,false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        myViewHolder.heading.setText((mList.get(position).getName().toUpperCase().charAt(0))+"");
        myViewHolder.name.setText(mList.get(position).getName());
        myViewHolder.publisher.setText(mList.get(position).getPublisher());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myViewHolder.headingContainer.setBackgroundTintList(stateList());
        }
        return convertView;
    }

    private int getRandom(){
        int color = r.nextInt(colorArray.length);
        return Color.parseColor(colorArray[color]);
    }

    public ColorStateList stateList(){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled},
                new int[] {-android.R.attr.state_enabled},
                new int[] {-android.R.attr.state_checked},
                new int[] { android.R.attr.state_pressed}
        };
        int color = getRandom();
        int[] colors = new int[] {color, color, color, color};
        return new ColorStateList(states, colors);
    }

    public class MyViewHolder{
        TextView heading,name,publisher;
        RelativeLayout headingContainer;
        MyViewHolder(View v){
            heading = (TextView)v.findViewById(R.id.requestHeading);
            name = (TextView)v.findViewById(R.id.requestName);
            publisher = (TextView)v.findViewById(R.id.requestPublisher);
            headingContainer = (RelativeLayout)v.findViewById(R.id.requestHeadingContainer);
        }
    }
}

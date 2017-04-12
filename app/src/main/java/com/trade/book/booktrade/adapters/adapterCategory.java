package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.objects.CategoryObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class adapterCategory extends BaseAdapter{

    private Context mContext;
    private ArrayList<CategoryObject> mList;

    public adapterCategory(Context context, ArrayList<CategoryObject> list){
        mContext = context;
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
        MyViewHolder myViewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.single_category,parent,false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        CategoryObject object = mList.get(position);
        myViewHolder.name.setText(object.getmName());
        Glide.with(mContext)
                .load(object.getmImageUrl())
                .centerCrop()
                .placeholder(R.color.colorAccent)
                .crossFade()
                .into(myViewHolder.banner);
        return convertView;
    }

    static class MyViewHolder{
        @BindView(R.id.singleCategoryPicture) ImageView banner;
        @BindView(R.id.singleCategoryName) TextView name;
        MyViewHolder(View v){
            ButterKnife.bind(this,v);
        }
    }
}

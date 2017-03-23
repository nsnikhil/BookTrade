package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trade.book.booktrade.R;

import java.util.ArrayList;

/**
 * Created by Nikhil on 23-Mar-17.
 */

public class adapterPurchaeeImage extends RecyclerView.Adapter<adapterPurchaeeImage.MyViewHolder> {

    Context mContext;
    ArrayList<String> url;

    public adapterPurchaeeImage(Context c, ArrayList<String> list) {
        mContext = c;
        url = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_image, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String surl = url.get(position);
        Glide.with(mContext)
                .load(surl)
                .centerCrop()
                .placeholder(R.color.colorBackground)
                .crossFade()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return url.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.singleImage);
            remove = (ImageView) itemView.findViewById(R.id.seingleImageRemove);
            remove.setVisibility(View.GONE);
        }
    }
}

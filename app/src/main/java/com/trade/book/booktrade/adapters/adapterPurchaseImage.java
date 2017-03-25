package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trade.book.booktrade.ImageActivity;
import com.trade.book.booktrade.R;

import java.util.ArrayList;

/**
 * Created by Nikhil on 23-Mar-17.
 */

public class adapterPurchaseImage extends RecyclerView.Adapter<adapterPurchaseImage.MyViewHolder> {

    Context mContext;
    ArrayList<String> url;
    int key;

    public adapterPurchaseImage(Context c, ArrayList<String> list,int k) {
        mContext = c;
        url = list;
        key = k;
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
                .placeholder(R.color.colorAccent)
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
            if(key==0){
                remove.setVisibility(View.GONE);
            }else {
                remove.setVisibility(View.VISIBLE);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    url.remove(getPosition());
                    notifyItemRemoved(getPosition());
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent img = new Intent(mContext, ImageActivity.class);
                    img.putExtra(mContext.getResources().getString(R.string.intentImageUrl),url.get(getPosition()));
                    img.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(img);
                }
            });
        }
    }
}

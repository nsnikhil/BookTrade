package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.trade.book.booktrade.ImageActivity;
import com.trade.book.booktrade.R;

import java.util.ArrayList;


public class adapterImage extends RecyclerView.Adapter<adapterImage.MyViewHolder> {

    ArrayList<Bitmap> bmp;
    Context mContext;
    int k;


    public adapterImage(Context c, ArrayList<Bitmap> img, int key) {
        mContext = c;
        bmp = img;
        k = key;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_image, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        holder.img.setImageBitmap(bmp.get(position));
    }


    @Override
    public int getItemCount() {
        return bmp.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView remove;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.singleImage);
            remove = (ImageView) itemView.findViewById(R.id.seingleImageRemove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bmp.remove(getPosition());
                    notifyItemRemoved(getPosition());
                }
            });
            if (k == 1) {
                remove.setVisibility(View.GONE);
            } else {
                remove.setVisibility(View.VISIBLE);
            }
        }
    }
}

package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trade.book.booktrade.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class adapterImage extends RecyclerView.Adapter<adapterImage.MyViewHolder> {

    private ArrayList<Bitmap> bmp;
    private Context mContext;
    private int k;


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
        @BindView(R.id.singleImage) ImageView img;
        @BindView(R.id.seingleImageRemove) ImageView remove;;
        @BindView(R.id.singleImageProgress) ProgressBar progress;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            progress.setVisibility(View.GONE);
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

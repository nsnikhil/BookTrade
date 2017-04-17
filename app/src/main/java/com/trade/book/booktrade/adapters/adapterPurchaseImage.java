package com.trade.book.booktrade.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.ImageActivity;
import com.trade.book.booktrade.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class adapterPurchaseImage extends RecyclerView.Adapter<adapterPurchaseImage.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> url;
    private int key;

    public adapterPurchaseImage(Context c, ArrayList<String> list, int k) {
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String surl = url.get(position);
        if (key == 2) {
            Glide.with(mContext)
                    .load(surl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(R.color.colorAccent)
                    .crossFade()
                    .into(holder.img);
        } else {
            Glide.with(mContext)
                    .load(surl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(R.color.colorAccent)
                    .centerCrop()
                    .crossFade()
                    .into(holder.img);
        }


    }

    @Override
    public int getItemCount() {
        return url.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.singleImage)
        ImageView img;
        @BindView(R.id.seingleImageRemove)
        ImageView remove;
        @BindView(R.id.singleImageProgress)
        ProgressBar progress;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            progress.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            if (key == 0) {
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                remove.setVisibility(View.GONE);
            } else if (key == 2) {
                remove.setVisibility(View.GONE);
                img.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            } else {
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                remove.setVisibility(View.VISIBLE);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    url.remove(getPosition());
                    notifyItemRemoved(getPosition());
                }
            });
            if (key != 2) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fullScreen = new Intent(mContext, ImageActivity.class);
                        //fullScreen.putExtra(mContext.getResources().getString(R.string.intentImageUrl), url.get(getPosition()));
                        fullScreen.putStringArrayListExtra(mContext.getResources().getString(R.string.intentArrayListUrl), url);
                        fullScreen.putExtra(mContext.getResources().getString(R.string.intentArrayListPosition), getPosition());
                        fullScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, img, "transitionBookImage");
                            mContext.startActivity(fullScreen, options.toBundle());
                        } else {
                            mContext.startActivity(fullScreen);
                        }
                    }
                });
            }
        }

    }
}

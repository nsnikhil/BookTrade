package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.objects.CategoryObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class adapterCategory extends BaseAdapter {

    private Context mContext;
    private ArrayList<CategoryObject> mList;

    public adapterCategory(Context context, ArrayList<CategoryObject> list) {
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
        final MyViewHolder myViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.single_category, parent, false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        CategoryObject object = mList.get(position);
        myViewHolder.name.setText(object.getmName());
        myViewHolder.catProgress.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        Glide.with(mContext)
                .load(object.getmImageUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        myViewHolder.catProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.color.colorAccentLight)
                .crossFade()
                .into(myViewHolder.banner);
        return convertView;
    }

    static class MyViewHolder {
        @BindView(R.id.singleCategoryPicture)
        ImageView banner;
        @BindView(R.id.singleCategoryName)
        TextView name;
        @BindView(R.id.singleCategoryProgress)
        ProgressBar catProgress;

        MyViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}

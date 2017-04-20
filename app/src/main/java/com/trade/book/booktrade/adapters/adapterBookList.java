package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.objects.BookObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class adapterBookList extends BaseAdapter {


    private Context mContext;
    private ArrayList<BookObject> list;
    private int k;

    public adapterBookList(Context c, ArrayList<BookObject> bookObjects, int key) {
        mContext = c;
        list = bookObjects;
        k = key;
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
        final MyViewHolder myViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.single_book, parent, false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        BookObject bookObject = list.get(position);
        myViewHolder.bookName.setText(bookObject.getName());
        myViewHolder.bookName.setAllCaps(true);
        setSellingPrice(myViewHolder, bookObject);
        myViewHolder.bookPublisher.setText(bookObject.getPublisher());
        myViewHolder.bookProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mContext.getApplicationContext(), R.color.white), PorterDuff.Mode.MULTIPLY);
        String url = mContext.getResources().getString(R.string.urlBucetHost) + mContext.getResources().getString(R.string.urlBucketName) + "/" + bookObject.getPhoto0();
        Log.d("", url);
        Glide.with(mContext)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        myViewHolder.bookProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.color.colorPrimaryDark)
                .crossFade()
                .into(myViewHolder.bookImage);
        setColor color = new setColor(myViewHolder, url);
        color.execute();
        return convertView;
    }

    private void setSellingPrice(MyViewHolder myViewHolder, BookObject bookObject) {
        if (k == 0) {
            myViewHolder.bookCostPrice.setVisibility(View.VISIBLE);
            myViewHolder.bookCostPrice.setText(mContext.getResources().getString(R.string.rupeeSymbol) + " " + bookObject.getCostPrice());
            myViewHolder.bookPrice.setText(mContext.getResources().getString(R.string.rupeeSymbol) + " " + bookObject.getSellingPrice());
        } else {
            myViewHolder.bookCostPrice.setVisibility(View.GONE);
            myViewHolder.bookPrice.setText(mContext.getResources().getString(R.string.rupeeSymbol) + " " + (bookObject.getSellingPrice() + compute(bookObject.getSellingPrice())));
        }
    }

    private double compute(int price) {
        if (price > 0 && price < 300) {
            return ((double) 8 / 100) * price;
        }
        if (price >= 300 && price <= 999) {
            return ((double) 6 / 100) * price;
        }
        if (price > 999) {
            return ((double) 4 / 100) * price;
        }
        return 0;
    }

    private void setColor(Palette p, MyViewHolder myViewHolder) {
        if (p != null) {
            myViewHolder.bookTextConatiner.setBackgroundColor(p.getDarkMutedColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.colorPrimary)));
        }
    }

    private Palette createPaletteAsync(String url) throws ExecutionException, InterruptedException {
        Bitmap b = Glide.with(mContext).load(url).asBitmap().into(100, 100).get();
        return Palette.from(b).generate();
    }

    static class MyViewHolder {
        @BindView(R.id.singleBookPicture)
        ImageView bookImage;
        @BindView(R.id.singleBookName)
        TextView bookName;
        @BindView(R.id.singleBookPublisher)
        TextView bookPublisher;
        @BindView(R.id.singleBookPrice)
        TextView bookPrice;
        @BindView(R.id.singleBookCostPrice)
        TextView bookCostPrice;
        @BindView(R.id.singleBookTextContainer)
        LinearLayout bookTextConatiner;
        @BindView(R.id.singleBookProgress)
        ProgressBar bookProgress;

        MyViewHolder(View v) {
            ButterKnife.bind(this, v);
            bookCostPrice.setPaintFlags(bookCostPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private class setColor extends AsyncTask<Void, Void, Palette> {

        MyViewHolder myViewHolder;
        String url;

        setColor(MyViewHolder mvh, String u) {
            myViewHolder = mvh;
            url = u;
        }


        @Override
        protected Palette doInBackground(Void... params) {
            try {
                return createPaletteAsync(url);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Palette palette) {
            setColor(palette, myViewHolder);
            super.onPostExecute(palette);
        }
    }
}

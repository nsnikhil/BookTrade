package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.cartData.CartTables;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class adapterCart extends CursorAdapter {

    public adapterCart(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_book, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        v.setTag(myViewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
        myViewHolder.bookName.setText(cursor.getString(cursor.getColumnIndex(CartTables.tablecart.mName)));
        myViewHolder.bookPublisher.setText(cursor.getString(cursor.getColumnIndex(CartTables.tablecart.mPublisher)));
        myViewHolder.bookPrice.setText(context.getResources().getString(R.string.rupeeSymbol) + " " + cursor.getInt(cursor.getColumnIndex(CartTables.tablecart.mSellingPrice)));
        String url = context.getResources().getString(R.string.urlBucetHost) + context.getResources().getString(R.string.urlBucketName) + "/" + cursor.getString(cursor.getColumnIndex(CartTables.tablecart.mPhoto0));
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.color.colorPrimaryDark)
                .crossFade()
                .into(myViewHolder.bookImage);
        setColor color = new setColor(context, myViewHolder, url);
        color.execute();
    }

    private void setColor(Context c, Palette p, MyViewHolder myViewHolder) {
        if (p != null) {
            myViewHolder.bookTextConatiner.setBackgroundColor(p.getDarkMutedColor(ContextCompat.getColor(c.getApplicationContext(), R.color.colorPrimary)));
        }
    }

    private Palette createPaletteAsync(Context c, String url) throws ExecutionException, InterruptedException {
        Bitmap b = Glide.with(c).load(url).asBitmap().into(100, 100).get();
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

        MyViewHolder(View v) {
            ButterKnife.bind(this, v);
            bookCostPrice.setVisibility(View.GONE);
        }
    }

    private class setColor extends AsyncTask<Void, Void, Palette> {

        MyViewHolder myViewHolder;
        String url;
        Context mContext;

        setColor(Context c, MyViewHolder mvh, String u) {
            myViewHolder = mvh;
            url = u;
            mContext = c;
        }


        @Override
        protected Palette doInBackground(Void... params) {
            try {
                return createPaletteAsync(mContext, url);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Palette palette) {
            setColor(mContext, palette, myViewHolder);
            super.onPostExecute(palette);
        }
    }
}

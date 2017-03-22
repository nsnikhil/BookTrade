package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trade.book.booktrade.R;
import com.trade.book.booktrade.cartData.CartTables;

/**
 * Created by Nikhil on 19-Mar-17.
 */

public class adapterCart extends CursorAdapter{

    public adapterCart(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_book,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        v.setTag(myViewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
        myViewHolder.bookName.setText(cursor.getString(cursor.getColumnIndex(CartTables.tablecart.mName)));
        myViewHolder.bookPublisher.setText(cursor.getString(cursor.getColumnIndex(CartTables.tablecart.mPublisher)));
        myViewHolder.bookPrice.setText(""+cursor.getInt(cursor.getColumnIndex(CartTables.tablecart.mSellingPrice)));
    }

    public class MyViewHolder{
        ImageView bookImage;
        TextView bookName,bookPublisher,bookPrice;
        MyViewHolder(View v){
            bookName = (TextView)v.findViewById(R.id.singleBookName);
            bookPrice = (TextView)v.findViewById(R.id.singleBookPrice);
            bookPublisher = (TextView)v.findViewById(R.id.singleBookPublisher);
            bookImage = (ImageView)v.findViewById(R.id.singleBookPicture);
        }
    }
}

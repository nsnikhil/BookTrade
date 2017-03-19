package com.trade.book.booktrade;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.trade.book.booktrade.data.Tables.tableOne;
import com.trade.book.booktrade.cartData.CartTables.tablecart;

import java.io.File;
import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter{


    Context mContext;
    ArrayList<BookObject> list;

    BookListAdapter(Context c,ArrayList<BookObject> bookObjects){
        mContext = c;
        list = bookObjects;
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
        MyViewHolder myViewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.single_book,parent,false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        BookObject bookObject = list.get(position);
        myViewHolder.bookName.setText(bookObject.getName());
        myViewHolder.bookPrice.setText("र "+bookObject.getSellingPrice());
        myViewHolder.bookPublisher.setText(bookObject.getPublisher());
        return convertView;
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

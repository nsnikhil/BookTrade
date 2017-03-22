package com.trade.book.booktrade.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.R;

import java.util.ArrayList;

public class adapterBookList extends BaseAdapter{


    Context mContext;
    ArrayList<BookObject> list;

    public adapterBookList(Context c, ArrayList<BookObject> bookObjects){
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
        myViewHolder.bookName.setAllCaps(true);
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

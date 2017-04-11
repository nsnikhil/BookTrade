package com.trade.book.booktrade.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by Nikhil on 11-Apr-17.
 */

public class CategoryObject {

    private String mName;
    private Drawable mImage;

    public CategoryObject(String nm,Drawable img){
        mName = nm;
        mImage = img;
    }

    public String getmName() {
        return mName;
    }

    public Drawable getmImage() {
        return mImage;
    }
}

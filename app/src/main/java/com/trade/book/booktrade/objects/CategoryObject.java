package com.trade.book.booktrade.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by Nikhil on 11-Apr-17.
 */

public class CategoryObject {

    private String mName,mImageUrl;

    public CategoryObject(String nm,String img){
        mName = nm;
        mImageUrl = img;
    }

    public String getmName() {
        return mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}

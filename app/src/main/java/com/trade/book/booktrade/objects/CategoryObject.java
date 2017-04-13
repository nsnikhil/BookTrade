package com.trade.book.booktrade.objects;


public class CategoryObject {

    private String mName, mImageUrl;

    public CategoryObject(String nm, String img) {
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

package com.trade.book.booktrade.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nsnik on 15-Dec-16.
 */

public class Tables {

    public static final String mTableName = "books";
    public static final String mDataBaseName = "bookdatabase";
    public static final int mDatabaseVersion = 7;

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.trade.book.booktrade";

    public static final Uri mBaseUri = Uri.parse(mScheme+mAuthority);
    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri,mTableName);

    public class tableOne implements BaseColumns{
        public static final String mUid = BaseColumns._ID;
        public static final String mName = "bookname";
        public static final String mPublisher = "bookpublisher";
        public static final String mCostPrice = "bookcost";
        public static final String mSellingPrice = "bookprice";
        public static final String mEdition = "bookedition";
        public static final String mCondition = "bookcondition";
        public static final String mCateogory = "bookcateogory";
        public static final String mDescription = "bookextra";
        public static final String mPicture0 = "bookpicturea";
        public static final String mPicture1 = "bookpictureb";
        public static final String mPicture2 = "bookpicturec";
        public static final String mPicture3 = "bookpictured";

    }

}

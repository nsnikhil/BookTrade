package com.trade.book.booktrade.cartData;

import android.net.Uri;
import android.provider.BaseColumns;


public class CartTables {

    public static final String mTableName = "cart";
    public static final String mDataBaseName = "cartdatabase";
    public static final int mDatabaseVersion = 7;

    public static final String mCartScheme = "content://";
    public static final String mCartAuthority = "com.trade.book.booktrade.cart";

    public static final Uri mCartBaseUri = Uri.parse(mCartScheme+mCartAuthority);
    public static final Uri mCartContentUri = Uri.withAppendedPath(mCartBaseUri,mTableName);

    public class tablecart implements BaseColumns {
        public static final String mUid = BaseColumns._ID;
        public static final String mName = "cartname";
        public static final String mPublisher = "cartpublisher";
        public static final String mCostPrice = "cartcost";
        public static final String mSellingPrice = "cartprice";
        public static final String mEdition = "cartedition";
        public static final String mCondition = "cartcondition";
        public static final String mCateogory = "cartcateogory";
        public static final String mDescription = "cartextra";
        public static final String mUserId = "cartuserId";
        public static final String mPhoto0 = "photourl0";
        public static final String mPhoto1 = "photourl1";
        public static final String mPhoto2 = "photourl2";
        public static final String mPhoto3 = "photourl3";
        public static final String mPhoto4 = "photourl4";
        public static final String mPhoto5 = "photourl5";
        public static final String mPhoto6 = "photourl6";
        public static final String mPhoto7 = "photourl7";
    }
}

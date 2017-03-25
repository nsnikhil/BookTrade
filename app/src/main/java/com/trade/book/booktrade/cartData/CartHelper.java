package com.trade.book.booktrade.cartData;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trade.book.booktrade.cartData.CartTables.tablecart;


public class CartHelper extends SQLiteOpenHelper{

    private static final String mCreateCartTable = "CREATE TABLE "+ CartTables.mTableName + " ("
            + tablecart.mUid + " INTEGER NOT NULL , "
            + tablecart.mBuid + " INTEGER NOT NULL , "
            + tablecart.mName + " TEXT NOT NULL, "
            + tablecart.mPublisher + " TEXT NOT NULL, "
            + tablecart.mCostPrice + " TEXT NOT NULL, "
            + tablecart.mSellingPrice + " INTEGER NOT NULL, "
            + tablecart.mEdition + " INTEGER NOT NULL, "
            + tablecart.mCondition + " TEXT NOT NULL , "
            + tablecart.mCateogory + " TEXT NOT NULL , "
            + tablecart.mDescription + " TEXT NOT NULL, "
            + tablecart.mUserId + " TEXT NOT NULL, "
            + tablecart.mPhoto0 + " TEXT NOT NULL, "
            + tablecart.mPhoto1 + " TEXT NOT NULL, "
            + tablecart.mPhoto2 + " TEXT NOT NULL, "
            + tablecart.mPhoto3 + " TEXT NOT NULL, "
            + tablecart.mPhoto4 + " TEXT NOT NULL, "
            + tablecart.mPhoto5 + " TEXT NOT NULL, "
            + tablecart.mPhoto6 + " TEXT NOT NULL, "
            + tablecart.mPhoto7 + " TEXT NOT NULL "
            +");";

    private static final String mDropCartTable = "DROP TABLE IF EXISTS "+ CartTables.mTableName;

    public CartHelper(Context context) {
        super(context, CartTables.mDataBaseName, null, CartTables.mDatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDataBase(sqLiteDatabase);
    }

    private void createDataBase(SQLiteDatabase sdb){
        sdb.execSQL(mCreateCartTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(mDropCartTable);
        createDataBase(sqLiteDatabase);
    }
}

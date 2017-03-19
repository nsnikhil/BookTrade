package com.trade.book.booktrade.cartData;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trade.book.booktrade.cartData.CartTables.tablecart;


public class CartHelper extends SQLiteOpenHelper{

    private static final String mCreateCartTable = "CREATE TABLE "+ CartTables.mTableName + " ("
            + tablecart.mUid + " INTEGER NOT NULL , "
            + tablecart.mName + " VARCHAR(250) NOT NULL, "
            + tablecart.mPublisher + " VARCHAR(250) NOT NULL, "
            + tablecart.mCostPrice + " INTEGER NOT NULL, "
            + tablecart.mSellingPrice + " INTEGER NOT NULL, "
            + tablecart.mEdition + " INTEGER, "
            + tablecart.mCondition + " INTEGER DEFAULT 112 , "
            + tablecart.mCateogory + " INTEGER DEFAULT 113 , "
            + tablecart.mDescription + " VARCHAR(250), "
            + tablecart.mUserId + " VARCHAR(250) "
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

package com.trade.book.booktrade.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.trade.book.booktrade.data.Tables.tableOne;


public class TableHelper extends SQLiteOpenHelper{



    private static final String mCreateTable = "CREATE TABLE "+ Tables.mTableName + " ("
            + tableOne.mUid + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + tableOne.mName + " VARCHAR(250) NOT NULL, "
            + tableOne.mPublisher + " VARCHAR(250) NOT NULL, "
            + tableOne.mCostPrice + " INTEGER NOT NULL, "
            + tableOne.mSellingPrice + " INTEGER NOT NULL, "
            + tableOne.mEdition + " INTEGER, "
            + tableOne.mCondition + " INTEGER DEFAULT 112, "
            + tableOne.mCateogory + " INTEGER DEFAULT 113,  "
            + tableOne.mDescription + " VARCHAR(250), "
            + tableOne.mPicture0 + " VARCHAR(250), "
            + tableOne.mPicture1 + " VARCHAR(250), "
            + tableOne.mPicture2 + " VARCHAR(250), "
            + tableOne.mPicture3 + " VARCHAR(250) "
            +");";

    private static final String mDropTable = "DROP TABLE IF EXISTS "+ Tables.mTableName;

    public TableHelper(Context context) {
        super(context, Tables.mDataBaseName, null, Tables.mDatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDataBase(sqLiteDatabase);
    }

    private void createDataBase(SQLiteDatabase sdb){
        sdb.execSQL(mCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(mDropTable);
        createDataBase(sqLiteDatabase);
    }
}

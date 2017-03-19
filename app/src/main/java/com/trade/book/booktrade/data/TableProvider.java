package com.trade.book.booktrade.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.trade.book.booktrade.data.Tables.tableOne;

public class TableProvider extends ContentProvider{

    TableHelper tableHelper;
    static  UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int uAllBooks = 1005;
    private static final int uSingleBook = 1006;

    static {
        sUriMatcher.addURI(Tables.mAuthority,Tables.mTableName,uAllBooks);
        sUriMatcher.addURI(Tables.mAuthority,Tables.mTableName+"/#",uSingleBook);
    }

    @Override
    public boolean onCreate() {
        tableHelper = new TableHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = tableHelper.getReadableDatabase();
        Cursor c = null;
        switch (sUriMatcher.match(uri)){
            case uAllBooks:
                c = sqLiteDatabase.query(Tables.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case uSingleBook:
                selection = tableOne.mUid + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sqLiteDatabase.query(Tables.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri "+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)){
            case uAllBooks:
                return insertVal(uri,contentValues);
            default:
                throw new IllegalArgumentException("Invalid uri"+uri);
        }
    }

    private Uri insertVal(Uri u,ContentValues cv){
        SQLiteDatabase sqLiteDatabase = tableHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(Tables.mTableName,null,cv);
        if(id==-1){
            return null;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return Uri.withAppendedPath(u,String.valueOf(id));
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

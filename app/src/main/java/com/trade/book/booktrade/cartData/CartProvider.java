package com.trade.book.booktrade.cartData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.trade.book.booktrade.cartData.CartTables.tablecart;


public class CartProvider extends ContentProvider{

    CartHelper cartHelper;
    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int uFullCart = 1007;
    private static final int uSingleCart = 1008;

    static {
        sUriMatcher.addURI(CartTables.mCartAuthority,CartTables.mTableName,uFullCart);
        sUriMatcher.addURI(CartTables.mCartAuthority,CartTables.mTableName+"/#",uSingleCart);
    }


    @Override
    public boolean onCreate() {
        cartHelper = new CartHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sdb = cartHelper.getReadableDatabase();
        Cursor c;
        switch (sUriMatcher.match(uri)){
            case uFullCart:
                c = sdb.query(CartTables.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case uSingleCart:
                selection = tablecart.mUid + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(CartTables.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri " +uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)){
            case uFullCart:
                return insertVal(uri,contentValues);
            default:
                throw new IllegalArgumentException("Invalid Uri "+ uri);
        }
    }

    private Uri insertVal(Uri u,ContentValues cv){
        SQLiteDatabase sdb = cartHelper.getWritableDatabase();
        long count = sdb.insert(CartTables.mTableName,null,cv);
        if(count==0){
            return null;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return Uri.withAppendedPath(u,String.valueOf(count));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        switch (sUriMatcher.match(uri)){
            case uFullCart:
                return deleteVal(uri,s,strings);
            case uSingleCart:
                s = tablecart.mUid + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteVal(uri,s,strings);
            default:
                throw new IllegalArgumentException("Invalid Uri " +uri);
        }
    }

    private int deleteVal(Uri u, String s, String[] sa){
        SQLiteDatabase sdb = cartHelper.getWritableDatabase();
        int count  = sdb.delete(CartTables.mTableName,s,sa);
        if(count==0){
            return count;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return count;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}

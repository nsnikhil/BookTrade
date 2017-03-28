package com.trade.book.booktrade;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.adapters.adapterCart;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.data.TableHelper;
import com.trade.book.booktrade.objects.BookObject;

import java.util.Calendar;


public class MyCartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{

    GridView bookCartGrid;
    ImageView noItem;
    Toolbar cartToolbar;
    FloatingActionButton checkOut;
    private static final int mCartLoaderId = 1;
    adapterCart cartAdapter;
    private static final String mNullValue = "N/A";

    public MyCartActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kart);
        initilize();
        loadCart();
        setEmpty();
    }


    private void initilize() {
        bookCartGrid = (GridView)findViewById(R.id.cartList);
        noItem = (ImageView)findViewById(R.id.cartEmpty);
        cartAdapter = new adapterCart(getApplicationContext(),null);
        bookCartGrid.setAdapter(cartAdapter);
        cartToolbar = (Toolbar)findViewById(R.id.toolbarCart);
        setSupportActionBar(cartToolbar);
        checkOut = (FloatingActionButton)findViewById(R.id.cartCheckOut);
        checkOut.setOnClickListener(this);
        bookCartGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if(c.moveToPosition(position)){
                    BookObject bookObject = new BookObject (c.getInt(c.getColumnIndex(tablecart.mBuid)),
                            c.getString(c.getColumnIndex(tablecart.mName)),
                            c.getString(c.getColumnIndex(tablecart.mPublisher)),
                            c.getInt(c.getColumnIndex(tablecart.mCostPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mSellingPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mEdition)),
                            c.getString(c.getColumnIndex(tablecart.mDescription)),
                            c.getString(c.getColumnIndex(tablecart.mCondition)),
                            c.getString(c.getColumnIndex(tablecart.mCateogory)),
                            c.getString(c.getColumnIndex(tablecart.mUserId)),
                            c.getInt(c.getColumnIndex(tablecart._ID))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto0))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto1))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto2))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto3))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto4))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto5))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto6))
                            ,c.getString(c.getColumnIndex(tablecart.mPhoto7))
                            ,c.getInt(c.getColumnIndex(tablecart.mstatus)));
                    Intent detail  = new Intent(getApplicationContext(),PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    detail.putExtra(getResources().getString(R.string.intenfromcart),true);
                    startActivity(detail);
                }
            }
        });
    }

    private void setEmpty(){
        if(getContentResolver().query(CartTables.mCartContentUri,null,null,null,null).getCount()<=0){
            noItem.setVisibility(View.VISIBLE);
        }else {
            noItem.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mCartLoaderId:
                return new CursorLoader(getApplicationContext(),CartTables.mCartContentUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cartAdapter.swapCursor(data);
        setEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cartAdapter.swapCursor(null);
    }

    private void loadCart() {
        if (getSupportLoaderManager().getLoader(mCartLoaderId) == null) {
            getSupportLoaderManager().initLoader(mCartLoaderId, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(mCartLoaderId, null, this).forceLoad();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cartCheckOut:
            Cursor c = getContentResolver().query(CartTables.mCartContentUri,null,null,null,null);
                int sp=0;
                while (c.moveToNext()){
                    sp = sp+c.getInt(c.getColumnIndex(tablecart.mSellingPrice));
                }
                buildCheckOutDialog("Total Price    : "+ sp+"\n"
                        +"\n"
                        +"Total Tax      : "+compute(sp)+"\n"
                        +"\n"
                        +"Final Amount   : "+(sp+compute(sp)));
            break;
        }
    }

    private double compute(int price) {
        if (price > 0 && price < 300) {
            return ((double) 8 / 100) * price;
        }
        if (price >= 300 && price <= 999) {
            return ((double) 6 / 100) * price;
        }
        if (price > 999) {
            return ((double) 4 / 100) * price;
        }
        return 0;
    }

    private void buildCheckOutDialog(String message){
        final AlertDialog.Builder abd = new AlertDialog.Builder(MyCartActivity.this);
        AlertDialog dialog;
        abd.setTitle("Check Out");
        abd.setMessage(message);
        abd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                uploadigDialog();
                checkOutFromCart();
            }
        });
        dialog = abd.create();
        dialog.show();
    }

    private void uploadigDialog(){
        AlertDialog.Builder uploading = new AlertDialog.Builder(MyCartActivity.this);
        uploading.setTitle("\n"+"Processing..."+"\n").setCancelable(false).create().show();
    }

    private void checkOutFromCart(){
        Cursor c = getContentResolver().query(CartTables.mCartContentUri,null,null,null,null);
        while (c.moveToNext()){
            buy(c);
            shift(c);
        }
        Toast.makeText(getApplicationContext(),"Purchase Successfull",Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
                startActivity(new Intent(MyCartActivity.this, MainActivity.class));
            }
        }, 2000);
    }

    private String buildUpdateUri(Cursor cursor) {
        String host = getResources().getString(R.string.urlServer);
        String moveToTransit = getResources().getString(R.string.urlMoveToTransit);
        String url = host + moveToTransit;

        String bidQuery = "id";

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bidQuery, String.valueOf(cursor.getInt(cursor.getColumnIndex(tablecart.mBuid))))
                .build().toString();
    }

    private String buildTransactionUri(Cursor cursor) {

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String host = getResources().getString(R.string.urlServer);

        String insertTransaction = getResources().getString(R.string.urlTransactionInsert);

        String url = host + insertTransaction;

        Calendar c = Calendar.getInstance();

        String buyerUidQuery = "buid";
        String buyerUidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);

        String sellerUidQuery = "suid";
        String sellerUidValue = cursor.getString(cursor.getColumnIndex(tablecart.mUserId));

        int price = cursor.getInt(cursor.getColumnIndex(tablecart.mSellingPrice));

        String buyerMoneyQuery = "bp";
        String buyerMoneyValue = ((price + compute(price)) + "");

        String sellerMoneyQuery = "sp";
        String sellerMoneyValue = ((price - compute(price)) + "");

        String dateQuery = "bt";
        String dateValue = String.valueOf(c.getTimeInMillis());

        String bookIdQuery = "bkid";
        int bookIdValue = cursor.getInt(cursor.getColumnIndex(tablecart.mBuid));

        String statusQuery = "st";

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(buyerUidQuery, buyerUidValue)
                .appendQueryParameter(sellerUidQuery, sellerUidValue)
                .appendQueryParameter(buyerMoneyQuery, buyerMoneyValue)
                .appendQueryParameter(sellerMoneyQuery, sellerMoneyValue)
                .appendQueryParameter(dateQuery, dateValue)
                .appendQueryParameter(bookIdQuery, String.valueOf(bookIdValue))
                .appendQueryParameter(statusQuery, String.valueOf(0))
                .build().toString();
    }

    private void buy(Cursor cursor) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildTransactionUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void shift(Cursor cursor) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}

package com.trade.book.booktrade.fragments;


import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.PurchaseActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;
import com.trade.book.booktrade.adapters.adapterCart;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.interfaces.RequestListScrollChange;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCartFragment extends Fragment implements View.OnClickListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.cartList) GridView bookCartGrid;
    @BindView(R.id.cartEmpty) ImageView noItem;
    @BindView(R.id.cartCheckOut) FloatingActionButton checkOut;
    private static final int mCartLoaderId = 2;
    adapterCart cartAdapter;
    private static final String mNullValue = "N/A";
    private int mCursorCount = 0;

    public MyCartFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_cart,container,false);
        ButterKnife.bind(this,v);
        initilize(v);
        setEmpty();
        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getLoaderManager().initLoader(mCartLoaderId, null, this);
    }

    private void initilize(View v) {
        cartAdapter = new adapterCart(getActivity(), null);
        bookCartGrid.setAdapter(cartAdapter);
        checkOut.setOnClickListener(this);
        bookCartGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if (c.moveToPosition(position)) {
                    BookObject bookObject = new BookObject(c.getInt(c.getColumnIndex(tablecart.mBuid)),
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
                            , c.getString(c.getColumnIndex(tablecart.mPhoto0))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto1))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto2))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto3))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto4))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto5))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto6))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto7))
                            , c.getInt(c.getColumnIndex(tablecart.mstatus)));
                    Intent detail = new Intent(getActivity(), PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    detail.putExtra(getResources().getString(R.string.intenfromcart), true);
                    startActivity(detail);
                }
            }
        });
    }

    private void setEmpty() {
        if (getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null).getCount() <= 0) {
            noItem.setVisibility(View.VISIBLE);
        } else {
            noItem.setVisibility(View.GONE);
        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mCartLoaderId:
                return new android.content.CursorLoader(getActivity(),CartTables.mCartContentUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        setEmpty();
        cartAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        cartAdapter.swapCursor(null);
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
           Toast.makeText(getActivity(),"Visible",Toast.LENGTH_SHORT).show();
        }

        super.setMenuVisibility(visible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartCheckOut:

                Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
                if (c.getCount() > 0) {
                    checkSold();
                } else {
                    Toast.makeText(getActivity(), "Cart Empty", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private double compute(int price) {
        if (price < 100) {
            return 7;
        }
        if (price > 100 && price < 300) {
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

    private void buildCheckOutDialog(String message) {
        final AlertDialog.Builder abd = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        abd.setTitle("Check Out");
        abd.setMessage(message);
        abd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                uploadigDialog().show();
                checkOutFromCart();
            }
        });
        dialog = abd.create();
        dialog.show();
    }

    private Dialog uploadigDialog() {
        AlertDialog.Builder uploading = new AlertDialog.Builder(getActivity());
        uploading.setMessage("Processing...").setCancelable(false).create().show();
        return uploading.create();
    }

    private void checkOutFromCart() {
        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        while (c.moveToNext()) {
            buy(c);
            shift(c);
        }
        Toast.makeText(getActivity(), "Purchase Successful", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                uploadigDialog().dismiss();
                getActivity().getContentResolver().delete(CartTables.mCartContentUri, null, null);
                getActivity().finish();
                startActivity(new Intent(getActivity(), StartActivity.class));
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

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildTransactionUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private String buildSoldUri(int id) {
        String host = getResources().getString(R.string.urlServer);
        String purchaseAvailable = getResources().getString(R.string.urlPurchaseAvailable);
        String url = host + purchaseAvailable;
        String bidQuery = "bd";
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bidQuery, String.valueOf(id))
                .build().toString();
    }

    private void removeSold(final int bid, final int count, final Dialog d) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildSoldUri(bid), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    removeIfAvailable(response, bid,count,d);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void removeIfAvailable(JSONArray status, int id,int count,Dialog d) throws JSONException {
        if (status.length() > 0) {
            JSONObject obj = status.getJSONObject(0);
            int statusCode = obj.getInt("status");
            mCursorCount++;
            if (statusCode == 1) {
                getActivity().getContentResolver().delete(Uri.withAppendedPath(CartTables.mCartContentUri, String.valueOf(id)), null, null);
                Toast.makeText(getActivity(), "Item removed from cart as it was sold", Toast.LENGTH_SHORT).show();
            }
        }
        if(mCursorCount==count){
            Cursor c1 = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
            mCursorCount=0;
            d.dismiss();
            if(c1.getCount()>0){
                showCheckOutDialog();
            }else {
                Toast.makeText(getActivity(),"Cart Empty",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSold() {
        final Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        AlertDialog.Builder wait = new AlertDialog.Builder(getActivity());
        wait.setMessage("Verifying");
        final Dialog d = wait.create();
        d.show();
        while (c.moveToNext()) {
            removeSold(c.getInt(c.getColumnIndex(tablecart.mBuid)),c.getCount(),d);
        }
    }

    private void showCheckOutDialog(){
        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        int sp = 0;
        while (c.moveToNext()) {
            sp = sp + c.getInt(c.getColumnIndex(tablecart.mSellingPrice));
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        buildCheckOutDialog("All the books left in your cart will delivered to "+
                spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress), mNullValue) + " within one week\n\n"+
                "Total Price    : " + "र "+sp + "\n"
                + "\n"
                + "Convenience Fee     : " + "र "+compute(sp) + "\n"
                + "\n"
                + "Final Amount   : " + "र "+(sp + compute(sp)));
    }

    private void shift(Cursor cursor) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}

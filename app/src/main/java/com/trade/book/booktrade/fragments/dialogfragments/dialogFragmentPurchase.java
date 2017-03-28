package com.trade.book.booktrade.fragments.dialogfragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.MainActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.cartData.CartTables;

import java.util.Calendar;


public class dialogFragmentPurchase extends DialogFragment {

    TextView title, actualPrice, extraPrice, totalPrice;
    Button buy;
    private static final String mNullValue = "N/A";

    public dialogFragmentPurchase() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fragment_purchase, container, false);
        initilize(v);
        setVal();
        return v;
    }

    private void initilize(View v) {
        title = (TextView) v.findViewById(R.id.dialogPurchaseHead);
        actualPrice = (TextView) v.findViewById(R.id.dialogPurchasePrice);
        extraPrice = (TextView) v.findViewById(R.id.dialogPurchaseExtra);
        totalPrice = (TextView) v.findViewById(R.id.dialogPurchaseTotal);
        buy = (Button) v.findViewById(R.id.dialogPurchaseBuy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
                shift();
            }
        });
    }

    private void setVal() {
        Bundle args = getArguments();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = args.getString(getActivity().getResources().getString(R.string.bundleBookName));
        String publisher = args.getString(getActivity().getResources().getString(R.string.bundleBookPublisher));
        int price = args.getInt(getActivity().getResources().getString(R.string.bundleBookPrice));
        title.setText("Its a non refundable purchase \n" + name.toUpperCase() + " by " + publisher + " will be delivered to " + spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress), mNullValue) + " within one week");
        actualPrice.setText((double) price + "");
        extraPrice.setText(compute(price) + "");
        totalPrice.setText((price + compute(price)) + "");
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

    private String buildUpdateUri() {
        Bundle args = getArguments();

        String host = getResources().getString(R.string.urlServer);
        String moveToTransit = getResources().getString(R.string.urlMoveToTransit);
        String url = host + moveToTransit;

        String bidQuery = "id";
        int bidValue = args.getInt(getActivity().getResources().getString(R.string.bundleBookBookId));

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bidQuery, String.valueOf(bidValue))
                .build().toString();
    }

    private String buildTransactionUri() {
        Bundle args = getArguments();
        int price = args.getInt(getActivity().getResources().getString(R.string.bundleBookPrice));
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String insertTransaction = getResources().getString(R.string.urlTransactionInsert);
        String url = host + insertTransaction;
        Calendar c = Calendar.getInstance();

        String buyerUidQuery = "buid";
        String buyerUidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);

        String sellerUidQuery = "suid";
        String sellerUidValue = args.getString(getActivity().getResources().getString(R.string.bundleBookSellerUid), mNullValue);

        String buyerMoneyQuery = "bp";
        String buyerMoneyValue = ((price + compute(price)) + "");

        String sellerMoneyQuery = "sp";
        String sellerMoneyValue = ((price - compute(price)) + "");

        String dateQuery = "bt";
        String dateValue = String.valueOf(c.getTimeInMillis());

        String bookIdQuery = "bkid";
        int bookIdValue = args.getInt(getActivity().getResources().getString(R.string.bundleBookBookId));

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

    private void buy() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildTransactionUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void shift() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                removefromCart();
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void removefromCart() {
        Bundle args = getArguments();
        int bidValue = args.getInt(getActivity().getResources().getString(R.string.bundleBookBookId));
        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex(CartTables.tablecart.mUid)) == bidValue) {
                getActivity().getContentResolver().delete(Uri.withAppendedPath(CartTables.mCartContentUri, String.valueOf(c.getInt(c.getColumnIndex(CartTables.tablecart.mUid)))), null, null);
                Toast.makeText(getActivity(), "Item removed from cart also", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

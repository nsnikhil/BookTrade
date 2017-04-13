package com.trade.book.booktrade.fragments.dialogfragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
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
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;
import com.trade.book.booktrade.cartData.CartTables;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class dialogFragmentPurchase extends DialogFragment {

    @BindView(R.id.dialogPurchaseHead) TextView title;
    @BindView(R.id.dialogPurchasePrice) TextView actualPrice;
    @BindView(R.id.dialogPurchaseExtra) TextView extraPrice;
    @BindView(R.id.dialogPurchaseTotal) TextView totalPrice;
    @BindView(R.id.dialogPurchaseBuy) Button buy;
    private static final String mNullValue = "N/A";
    private Unbinder mUnbinder;

    public dialogFragmentPurchase() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fragment_purchase, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initilize();
        setVal();
        return v;
    }

    private void initilize() {
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
                shift();
            }
        });
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            buy.setTextColor(getResources().getColor(R.color.cardview_dark_background));
        }
    }

    private void setVal() {
        Bundle args = getArguments();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = args.getString(getActivity().getResources().getString(R.string.bundleBookName));
        String publisher = args.getString(getActivity().getResources().getString(R.string.bundleBookPublisher));
        int price = args.getInt(getActivity().getResources().getString(R.string.bundleBookPrice));
        title.setText(name.toUpperCase() + " by " + publisher + " will be delivered to " + spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress), mNullValue) + " within one week");
        actualPrice.setText("र "+(double) price );
        double taxPrice = Double.parseDouble(String.format("%.2f", compute(price)));
        taxPrice = Math.round(taxPrice);
        extraPrice.setText("र "+taxPrice);
        totalPrice.setText("र "+(price + taxPrice ));
    }

    private double compute(int price) {
        if(price<100){
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
                sendNotification();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void sendNotification() {
        Bundle args = getArguments();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = args.getString(getActivity().getResources().getString(R.string.bundleBookName));
        String publisher = args.getString(getActivity().getResources().getString(R.string.bundleBookPublisher));
        String messageBody = name.toUpperCase() + " by " + publisher + " will be delivered to " + spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress), mNullValue) + " within one week";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Order Confirmed")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void shift() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                removefromCart();
                getActivity().finish();
                startActivity(new Intent(getActivity(), StartActivity.class));
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

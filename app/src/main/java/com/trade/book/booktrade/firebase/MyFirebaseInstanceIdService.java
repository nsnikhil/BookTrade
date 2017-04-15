package com.trade.book.booktrade.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.network.VolleySingleton;


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
    private static final String mNullValue = "N/A";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spf.edit().putString(getResources().getString(R.string.prefFirebaseToken),refreshedToken).apply();
        if(!spf.getString(getResources().getString(R.string.prefAccountId),mNullValue).equalsIgnoreCase(mNullValue)){
            updateValues(refreshedToken);
        }
    }

    private String buildUri(String fkey) {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String host = getResources().getString(R.string.urlServer);
        String updateUser = getResources().getString(R.string.urlUserInsertFkey);
        String url = host + updateUser;

        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);

        String fkeyQuery = "fk";

        return Uri.parse(url).buildUpon().appendQueryParameter(fkeyQuery, fkey)
                .appendQueryParameter(uidQuery, uidValue).build().toString();
    }

    private void updateValues(final String key) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUri(key), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit().putString(getResources().getString(R.string.prefFirebaseToken),key).apply();
                Log.d(TAG,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}

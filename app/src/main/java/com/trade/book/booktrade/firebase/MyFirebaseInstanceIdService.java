package com.trade.book.booktrade.firebase;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.trade.book.booktrade.R;


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spf.edit().putString(getResources().getString(R.string.prefFirebaseToken),refreshedToken).apply();
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "Refreshed token: " + token);
    }

}

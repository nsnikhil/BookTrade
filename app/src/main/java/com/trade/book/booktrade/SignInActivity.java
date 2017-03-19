package com.trade.book.booktrade;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 540;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initilize();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestEmail().requestProfile().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void initilize() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            GoogleSignInAccount acct = result.getSignInAccount();
            spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),true).apply();
            spf.edit().putString(getResources().getString(R.string.prefAccountId),acct.getId()).apply();
            spf.edit().putString(getResources().getString(R.string.prefAccountName), acct.getDisplayName()).apply();
            spf.edit().putString(getResources().getString(R.string.prefAccountEmail), acct.getEmail()).apply();
            new DownloadImage().execute(acct.getPhotoUrl().toString());
        } else {

        }
    }

    public void closeSingInActivity() {
        this.finish();
        startActivity(new Intent(SignInActivity.this,MainActivity.class));
    }

    public class DownloadImage extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... params) {
            Bitmap poster = null;
            try {
                poster = getBitmap(new URL(params[0]));
                saveImage("profile.jpg",poster);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            closeSingInActivity();
        }

        private Bitmap getBitmap(URL imageUrl) {
            Bitmap pstr = null;
            HttpURLConnection htpc = null;
            InputStream ir;
            try {
                htpc = (HttpURLConnection) imageUrl.openConnection();
                htpc.setRequestMethod("GET");
                htpc.connect();
                if(htpc.getResponseCode()==200){
                    ir = htpc.getInputStream();
                    pstr = BitmapFactory.decodeStream(ir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                htpc.disconnect();
            }
            return pstr;
        }

        private  void saveImage(String s,Bitmap b) {
            File folder  = getExternalCacheDir();
            File f = new File(folder,s);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                b.compress(Bitmap.CompressFormat.JPEG, 20, fos);
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

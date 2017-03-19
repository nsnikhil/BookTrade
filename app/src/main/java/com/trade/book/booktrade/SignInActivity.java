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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

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
    LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initilize();
        setGooglePlus();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setUpFblogin();
    }

    private void initilize() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions();
        callbackManager = CallbackManager.Factory.create();
    }

    private void setUpFblogin(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String profilePicUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                                    SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),true).apply();
                                    spf.edit().putString(getResources().getString(R.string.prefAccountId),String.valueOf(response.getJSONObject().getInt("id"))).apply();
                                    spf.edit().putString(getResources().getString(R.string.prefAccountName), response.getJSONObject().getString("name")).apply();
                                    new DownloadImage().execute(profilePicUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture.width(600).height(600)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void setGooglePlus(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestEmail().requestProfile().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login_button:
                setUpFblogin();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            GoogleSignInAccount acct = result.getSignInAccount();
            spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),true).apply();
            spf.edit().putString(getResources().getString(R.string.prefAccountId),acct.getId()).apply();
            spf.edit().putString(getResources().getString(R.string.prefAccountName), acct.getDisplayName()).apply();
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

package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String mNullValue = "N/A";
    Button signOut,purchase,uploads;
    TextView name;
    CircularImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initilize();
        setVal();
    }

    private void setVal() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name.setText(spf.getString(getResources().getString(R.string.prefAccountName),mNullValue));
        if(getBitmap()!=null){
            profile.setImageBitmap(getBitmap());
        }
    }

    private Bitmap getBitmap() {
        Bitmap img = null;
        File folder = getExternalCacheDir();
        File fi = new File(folder, "profile.jpg");
        String fpath = String.valueOf(fi);
        img = BitmapFactory.decodeFile(fpath);
        return img;
    }

    private void initilize() {
        signOut = (Button)findViewById(R.id.accountSignOut);
        purchase = (Button)findViewById(R.id.accountPurchase);
        uploads = (Button)findViewById(R.id.accountUploads);
        name = (TextView) findViewById(R.id.accountName);
        profile = (CircularImageView) findViewById(R.id.accountPicture);
        signOut.setOnClickListener(this);
        purchase.setOnClickListener(this);
        uploads.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountUploads:
                Intent act = new Intent(AccountActivity.this,CategoryViewActivity.class);
                act.putExtra(getResources().getString(R.string.intencateuri),buildUri());
                act.putExtra(getResources().getString(R.string.intenupind),121);
                startActivity(act);
                break;
        }
    }

    private String buildUri(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlUserUploads);
        String url = host + searchFileName;
        String uidQuery = "uid";
        String uidValue  = spf.getString(getResources().getString(R.string.prefAccountId),mNullValue);
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(uidQuery, uidValue)
                .build()
                .toString();
    }

    private void makeDialog() {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(AccountActivity.this);
        alerDialog.setTitle("Warning");
        alerDialog.setMessage("Are you sre you want to sign out");
        alerDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken!=null){
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),false).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountId),mNullValue).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountName), mNullValue).apply();
                deleteFile();
                setResult(RESULT_OK, null);
                finish();
                startActivity(new Intent(AccountActivity.this,MainActivity.class));
            }
        }).create().show();
    }

    private void deleteFile(){
        File folder  = getExternalCacheDir();
        File f = new File(folder,"profile.jpg");
        if(f.exists()){
            f.delete();
        }
    }
}

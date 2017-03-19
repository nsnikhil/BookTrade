package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String mNullValue = "N/A";
    private static final int mZeroValue = 0;
    Toolbar accountToolbar;
    CollapsingToolbarLayout  accountCollapsingToolbarLayout;
    ImageView accountBanner;
    ListView purchaseList,uploadList,cartList;
    LinearLayout purchaseLayout,uploadLayout,cartLayout;
    Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initilize();
        setValue();
        addFakedata();
    }

    private void initilize() {
        accountToolbar = (Toolbar)findViewById(R.id.accounttoolbar);
        accountCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.accountCollapsingToolbar);
        accountBanner = (ImageView)findViewById(R.id.accountBanner);
        accountBanner.setScaleType(ImageView.ScaleType.CENTER_CROP);
        purchaseList = (ListView)findViewById(R.id.accountPurchaseList);
        uploadList = (ListView)findViewById(R.id.accountUploadList);
        cartList = (ListView)findViewById(R.id.accountCartList);
        signOut = (Button)findViewById(R.id.accountSignOut);
        signOut.setOnClickListener(this);
        purchaseLayout = (LinearLayout)findViewById(R.id.accountPurchaseContainer);
        uploadLayout = (LinearLayout)findViewById(R.id.accountUploadContainer);
        cartLayout = (LinearLayout)findViewById(R.id.accountCartContainer);
        purchaseLayout.setOnClickListener(this);
        cartLayout.setOnClickListener(this);
        uploadLayout.setOnClickListener(this);
    }

    private void addFakedata(){
        ArrayList<String> list = new ArrayList<>();
        list.add("No Items");
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        purchaseList.setAdapter(adp);
        uploadList.setAdapter(adp);
        cartList.setAdapter(adp);
    }

    private void setValue(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountToolbar.setTitle(spf.getString(getResources().getString(R.string.prefAccountName),mNullValue));
        accountBanner.setImageBitmap(getBitmap());
    }


    private Bitmap getBitmap() {
        Bitmap img = null;
        File folder = getExternalCacheDir();
        File fi = new File(folder,"profile.jpg");
        String fpath = String.valueOf(fi);
        img = BitmapFactory.decodeFile(fpath);
        return img;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountSignOut:
                makeDialog();
                break;
            case R.id.accountPurchaseContainer:
                Toast.makeText(getApplicationContext(),"Purchaese",Toast.LENGTH_SHORT).show();
                break;
            case R.id.accountCartContainer:
                Toast.makeText(getApplicationContext(),"cart",Toast.LENGTH_SHORT).show();
                break;
            case R.id.accountUploadContainer:
                Toast.makeText(getApplicationContext(),"upload",Toast.LENGTH_SHORT).show();
                break;
        }
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

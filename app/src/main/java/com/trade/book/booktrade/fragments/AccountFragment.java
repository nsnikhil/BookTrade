package com.trade.book.booktrade.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.trade.book.booktrade.CategoryViewActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;
import com.trade.book.booktrade.cartData.CartTables;

import java.io.File;

public class AccountFragment extends Fragment implements View.OnClickListener{

    private static final String mNullValue = "N/A";
    Button signOut,purchase,uploads;
    TextView name;
    CircularImageView profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_account,container,false);
        initilize(v);
        setVal();
        return v;
    }

    private void setVal() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        name.setText(spf.getString(getResources().getString(R.string.prefAccountName),mNullValue));
        if(getBitmap()!=null){
            profile.setImageBitmap(getBitmap());
        }
    }

    private Bitmap getBitmap() {
        Bitmap img = null;
        File folder = getActivity().getExternalCacheDir();
        File fi = new File(folder, "profile.jpg");
        String fpath = String.valueOf(fi);
        img = BitmapFactory.decodeFile(fpath);
        return img;
    }

    private void initilize(View v) {
        signOut = (Button)v.findViewById(R.id.accountSignOut);
        purchase = (Button)v.findViewById(R.id.accountPurchase);
        uploads = (Button)v.findViewById(R.id.accountUploads);
        name = (TextView) v.findViewById(R.id.accountName);
        profile = (CircularImageView) v.findViewById(R.id.accountPicture);
        signOut.setOnClickListener(this);
        purchase.setOnClickListener(this);
        uploads.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountUploads:
                Intent act = new Intent(getActivity(),CategoryViewActivity.class);
                act.putExtra(getResources().getString(R.string.intencateuri),buildUri());
                act.putExtra(getResources().getString(R.string.intenupind),121);
                startActivity(act);
                break;
            case R.id.accountSignOut:
                makeDialog();
                break;
            case R.id.accountPurchase:
                Intent pur = new Intent(getActivity(),CategoryViewActivity.class);
                pur.putExtra(getResources().getString(R.string.intencateuri),buildPurchaseUri());
                pur.putExtra(getResources().getString(R.string.intenupind),123);
                startActivity(pur);
                break;
        }
    }

    private String buildPurchaseUri() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlTransactionQueryYour);
        String url = host + searchFileName;
        String uidQuery = "uid";
        String uidValue  = spf.getString(getResources().getString(R.string.prefAccountId),mNullValue);
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(uidQuery, uidValue)
                .build()
                .toString();
    }

    private String buildUri(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(getActivity());
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
                    FacebookSdk.sdkInitialize(getActivity());
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),false).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountId),mNullValue).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountName), mNullValue).apply();
                deleteFile();
                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().getContentResolver().delete(CartTables.mCartContentUri,null,null);
                getActivity().finish();
                startActivity(new Intent(getActivity(),StartActivity.class));
            }
        }).create().show();
    }

    private void deleteFile(){
        File folder  = getActivity().getExternalCacheDir();
        File f = new File(folder,"profile.jpg");
        if(f.exists()){
            f.delete();
        }
    }
}

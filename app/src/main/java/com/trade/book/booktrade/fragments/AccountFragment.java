package com.trade.book.booktrade.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String mNullValue = "N/A";
    @BindView(R.id.accountSignOut)
    Button signOut;
    @BindView(R.id.accountPurchase)
    Button purchase;
    @BindView(R.id.accountUploads)
    Button uploads;
    @BindView(R.id.accountName)
    TextView name;
    @BindView(R.id.accountPicture)
    CircularImageView profile;
    private Unbinder mUnbinder;
    private boolean mTheme = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_account, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initialize();
        setVal();
        checkPrefrence();
        return v;
    }

    private void checkPrefrence() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefValue = spf.getString(getResources().getString(R.string.prefThemeKey), mNullValue);
        if (prefValue.equalsIgnoreCase("Default")) {
            mTheme = false;
            name.setTextColor(getResources().getColor(R.color.colorAccent));
            signOut.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbutton));
            purchase.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbutton));
            uploads.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbutton));
        } else if (prefValue.equalsIgnoreCase("Multi-Color")) {
            mTheme = true;
            name.setTextColor(Color.parseColor("#455A64"));
            signOut.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbuttoncolor));
            purchase.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbuttoncolor));
            uploads.setBackground(getActivity().getResources().getDrawable(R.drawable.roundbuttoncolor));
        }
    }

    private void setVal() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        name.setText(spf.getString(getResources().getString(R.string.prefAccountName), mNullValue));
        if (getBitmap() != null) {
            profile.setImageBitmap(getBitmap());
        }
    }

    private Bitmap getBitmap() {
        File folder = getActivity().getExternalCacheDir();
        File fi = new File(folder, "profile.jpg");
        String fpath = String.valueOf(fi);
        return BitmapFactory.decodeFile(fpath);
    }

    private void initialize() {
        signOut.setOnClickListener(this);
        purchase.setOnClickListener(this);
        uploads.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accountUploads:
                Intent act = new Intent(getActivity(), CategoryViewActivity.class);
                act.putExtra(getResources().getString(R.string.intencateuri), buildUri());
                act.putExtra(getResources().getString(R.string.intenupind), 121);
                startActivity(act);
                break;
            case R.id.accountSignOut:
                makeDialog();
                break;
            case R.id.accountPurchase:
                Intent pur = new Intent(getActivity(), CategoryViewActivity.class);
                pur.putExtra(getResources().getString(R.string.intencateuri), buildPurchaseUri());
                pur.putExtra(getResources().getString(R.string.intenupind), 123);
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
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(uidQuery, uidValue)
                .build()
                .toString();
    }

    private String buildUri() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlUserUploads);
        String url = host + searchFileName;
        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(uidQuery, uidValue)
                .build()
                .toString();
    }

    private void makeDialog() {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(getActivity());
        alerDialog.setTitle("Warning");
        alerDialog.setMessage("Are you sure you want to sign out");
        alerDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    FacebookSdk.sdkInitialize(getActivity());
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator), false).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountId), mNullValue).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountName), mNullValue).apply();
                deleteFile();
                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().getContentResolver().delete(CartTables.mCartContentUri, null, null);
                getActivity().finish();
                startActivity(new Intent(getActivity(), StartActivity.class));
            }
        }).create().show();
    }

    private void deleteFile() {
        File folder = getActivity().getExternalCacheDir();
        File f = new File(folder, "profile.jpg");
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        }

        if (animation != null) {
            getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    getView().setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }


            });
        }

        return animation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}

package com.trade.book.booktrade.fragments.introfragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class intro4Fragment extends Fragment implements ISlidePolicy {


    private static final String mNullValue = "N/A";
    boolean agreed = false;
    @BindView(R.id.intro4heading)
    TextView terms;
    private Unbinder mUnbinder;

    public intro4Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro4, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }

    @Override
    public boolean isPolicyRespected() {
        return agreed;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        buildAgreeDialog();
    }

    private void buildAgreeDialog() {
        final AlertDialog.Builder agree = new AlertDialog.Builder(getActivity())
                .setMessage("Do you agree with the terms and condition")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
                        getActivity().finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agreed = true;
                        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator), true).apply();
                        getActivity().finish();
                        getActivity().startActivity(new Intent(getActivity(), StartActivity.class));
                    }
                });
        agree.create().show();
    }

    private void deleteAll() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator), false).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountId), mNullValue).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountName), mNullValue).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountPhone), mNullValue).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountAddress), mNullValue).apply();
        deleteFile();
    }

    private void deleteFile() {
        File folder = getActivity().getExternalCacheDir();
        File f = new File(folder, "profile.jpg");
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

package com.trade.book.booktrade.fragments.introfragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.trade.book.booktrade.MainActivity;
import com.trade.book.booktrade.R;

public class intro4Fragment extends Fragment implements ISlidePolicy {

    Button agree;
    boolean agreed = false;

    public intro4Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro4, container, false);
        agree = (Button) v.findViewById(R.id.intro4Agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator),true).apply();
                agreed = true;
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return v;
    }

    @Override
    public boolean isPolicyRespected() {
        return agreed;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}

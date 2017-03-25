package com.trade.book.booktrade.fragments.introfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.trade.book.booktrade.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class intro1Fragment extends Fragment implements ISlidePolicy {


    public intro1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro1, container, false);
    }

    @Override
    public boolean isPolicyRespected() {
        return true;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}

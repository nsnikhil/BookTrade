package com.trade.book.booktrade.fragments.introfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.trade.book.booktrade.R;

import butterknife.ButterKnife;


public class intro1Fragment extends Fragment implements ISlidePolicy {


    ShimmerTextView mShimmerTextView;
    Shimmer shimmer = new Shimmer();

    public intro1Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_intro1, container, false);
        mShimmerTextView = (ShimmerTextView)v.findViewById(R.id.intro1Text);
        shimmer.setDuration(2000).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmer.start(mShimmerTextView);
    }

    @Override
    public boolean isPolicyRespected() {
        return true;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}

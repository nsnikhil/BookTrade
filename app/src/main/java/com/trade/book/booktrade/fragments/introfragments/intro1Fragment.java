package com.trade.book.booktrade.fragments.introfragments;


import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.trade.book.booktrade.R;


public class intro1Fragment extends Fragment implements ISlidePolicy {


    ShimmerTextView mShimmerTextView;

    public intro1Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_intro1, container, false);
        mShimmerTextView = (ShimmerTextView)v.findViewById(R.id.intro1Text);
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(mShimmerTextView);
        return v;
    }

    @Override
    public boolean isPolicyRespected() {
        return true;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}

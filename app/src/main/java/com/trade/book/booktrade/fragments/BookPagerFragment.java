package com.trade.book.booktrade.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterBookPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BookPagerFragment extends Fragment {

    @BindView(R.id.mainBookPager)
    ViewPager bookPager;
    @BindView(R.id.mainPagerTabs)
    TabLayout bookTabs;
    private Unbinder mUnbinder;

    public BookPagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_pager, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initialize();
        return v;
    }

    private void initialize() {
        adapterBookPager bookPagerAdapter = new adapterBookPager(getActivity().getSupportFragmentManager());
        bookPager.setAdapter(bookPagerAdapter);
        bookTabs.setupWithViewPager(bookPager);
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

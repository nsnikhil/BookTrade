package com.trade.book.booktrade.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BookPagerFragment extends Fragment {

    @BindView(R.id.mainBookPager) ViewPager bookPager;
    @BindView(R.id.mainPagerTabs) TabLayout bookTabs;
    private Unbinder mUnbinder;

    public BookPagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_book_pager, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize(v);
        return v;
    }

    private void initialize(View v) {
        adapterBookPager bookPagerAdapter = new adapterBookPager(getActivity().getSupportFragmentManager());
        bookPager.setAdapter(bookPagerAdapter);
        bookTabs.setupWithViewPager(bookPager);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}

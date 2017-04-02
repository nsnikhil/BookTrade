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


public class BookPagerFragment extends Fragment {

    ViewPager bookPager;
    TabLayout bookTabs;

    public BookPagerFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_book_pager, container, false);
        initialize(v);
        return v;
    }

    private void initialize(View v) {
        bookPager = (ViewPager)v.findViewById(R.id.mainBookPager);
        bookTabs = (TabLayout)v.findViewById(R.id.mainPagerTabs);
        adapterBookPager bookPagerAdapter = new adapterBookPager(getActivity().getSupportFragmentManager());
        bookPager.setAdapter(bookPagerAdapter);
        bookTabs.setupWithViewPager(bookPager);
    }

}

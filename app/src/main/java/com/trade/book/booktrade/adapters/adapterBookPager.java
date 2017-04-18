package com.trade.book.booktrade.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.trade.book.booktrade.fragments.BookListFragment;
import com.trade.book.booktrade.fragments.CategoryFragment;


public class adapterBookPager extends FragmentStatePagerAdapter {

    private static final CharSequence[] mPageTitle = {"Books", "Category"};


    public adapterBookPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if (position == 0) {
            f = new BookListFragment();
        }
        if (position == 1) {
            f = new CategoryFragment();
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence s = null;
        if (position == 0) {
            s = mPageTitle[0];
        }
        if (position == 1) {
            s = mPageTitle[1];
        }
        return s;
    }
}

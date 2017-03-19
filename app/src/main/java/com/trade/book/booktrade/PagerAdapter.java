package com.trade.book.booktrade;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



public class PagerAdapter extends FragmentStatePagerAdapter{

    private static final CharSequence[] mPageTitle = {"Books","Cateogory"};


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if(position==0){
            f = new BookList();
        }
        if(position==1){
            f = new CateogoryFragment();
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence s= null;
        if(position==0) {
            s = mPageTitle[0];
        }
        if(position==1) {
            s = mPageTitle[1];
        }
        return s;
    }
}

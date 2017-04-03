package com.trade.book.booktrade.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trade.book.booktrade.CategoryViewActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterList;
import com.trade.book.booktrade.interfaces.RequestListScrollChange;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryFragment extends Fragment {

    ListView catList;

    RequestListScrollChange scrollChange;

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cateogory, container, false);
        initilize(v);
        addList();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            scrollChange = (RequestListScrollChange) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    private void initilize(final View v) {
        catList = (ListView)v.findViewById(R.id.cateogoryList);
        catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                if(s.indexOf(' ')!=-1){
                    s = s.substring(0,s.indexOf(' '));
                }
                String su = buildUri(s);
                Intent cat = new Intent(getActivity(),CategoryViewActivity.class);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuri),su);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuripos),position);
                startActivity(cat);
            }
        });
        catList.setOnScrollListener(new AbsListView.OnScrollListener() {
            int prevVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(prevVisibleItem != firstVisibleItem){
                    if(prevVisibleItem < firstVisibleItem){
                        scrollChange.hideItems();
                    }
                    else{
                        scrollChange.showItems();
                    }
                    prevVisibleItem = firstVisibleItem;
                }
            }
        });
    }

    private String buildUri(String val){
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlSearchCatQuery);
        String url = host + searchFileName;
        String catQuery = "ct";
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(catQuery, val)
                .build()
                .toString();
    }

    private void addList(){
        String[] arr = getResources().getStringArray(R.array.bookCateogories);
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(arr));
        adapterList adptr = new adapterList(getActivity(),stringList);
        catList.setAdapter(adptr);
    }

}

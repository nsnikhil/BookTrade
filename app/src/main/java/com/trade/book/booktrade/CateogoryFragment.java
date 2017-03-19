package com.trade.book.booktrade;


import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CateogoryFragment extends Fragment {

    ListView catList;

    public CateogoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cateogory, container, false);
        initilize(v);
        addList();
        return v;
    }

    private void initilize(View v) {
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
        CateogoryAdapter adptr = new CateogoryAdapter(getActivity(),stringList);
        catList.setAdapter(adptr);
    }

}

package com.trade.book.booktrade.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterList;
import com.trade.book.booktrade.fragments.dialogfragments.*;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {

    ListView moreList;

    public MoreFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cateogory, container, false);
        initilize(v);
        addList();
        return v;
    }

    private void initilize(View v) {
        moreList = (ListView)v.findViewById(R.id.cateogoryList);
    }

    private void addList(){
        ArrayList<String> moreItem = new ArrayList<>();
        moreItem.add("You Address");
        moreItem.add("Refer a Freind");
        moreItem.add("Help");
        moreItem.add("Chat with us");
        moreItem.add("Settings");
        adapterList adptr = new adapterList(getActivity(),moreItem);
        moreList.setAdapter(adptr);
        moreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });
    }

    private void itemClick(int position){
        switch (position){
            case 0:
                dialogFagmentAddress address = new dialogFagmentAddress();
                address.show(getFragmentManager(),"dialog");
                break;
            case 1:
                Toast.makeText(getActivity(),"Will share the app once on play store",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(),"Will show help once on play store",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(),"Will start chat once we have public email",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(getActivity(),"Nothing in settings",Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

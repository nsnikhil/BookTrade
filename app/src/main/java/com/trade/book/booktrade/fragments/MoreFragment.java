package com.trade.book.booktrade.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.trade.book.booktrade.AddBook;
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
        moreItem.add("Your Address");
        moreItem.add("Refer a Friend");
        moreItem.add("Help");
        moreItem.add("Chat with us");
        moreItem.add("Send Feedback");
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
                chooseHelpDialog();
                break;
            case 3:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "shelf.bee.corp@gmail.com"});
                email.setType("message/rfc822");
                if(email.resolveActivity(getActivity().getPackageManager())!=null){
                    startActivity(email);
                }else {
                    Toast.makeText(getActivity(),"No email app found",Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                Intent feedback = new Intent(Intent.ACTION_SEND);
                feedback.putExtra(Intent.EXTRA_EMAIL, new String[]{ "shelf.bee.corp@gmail.com"});
                feedback.setType("message/rfc822");
                if(feedback.resolveActivity(getActivity().getPackageManager())!=null){
                    startActivity(feedback);
                }else {
                    Toast.makeText(getActivity(),"No email app found",Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                Toast.makeText(getActivity(),"Nothing in settings",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void chooseHelpDialog() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Learn How to use the app");
        arrayAdapter.add("Problem with payment");
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Toast.makeText(getActivity(),"Will show how to use",Toast.LENGTH_SHORT).show();
                }
                if (position == 1) {
                    Toast.makeText(getActivity(),"Payment issue",Toast.LENGTH_SHORT).show();
                }
            }
        });
        choosePath.create().show();
    }

}

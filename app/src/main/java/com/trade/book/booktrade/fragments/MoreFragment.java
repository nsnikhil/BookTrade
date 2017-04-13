package com.trade.book.booktrade.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.trade.book.booktrade.PrefActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterList;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFagmentAddress;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MoreFragment extends Fragment {

    @BindView(R.id.cateogoryList)
    ListView moreList;
    private Unbinder mUnbinder;

    public MoreFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cateogory, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        addList();
        return v;
    }

    private void addList() {
        ArrayList<String> moreItem = new ArrayList<>();
        moreItem.add("Your Address");
        moreItem.add("Refer a Friend");
        moreItem.add("Help");
        moreItem.add("Chat with us");
        moreItem.add("Send Feedback");
        moreItem.add("Settings");
        moreItem.add("About");
        adapterList adptr = new adapterList(getActivity(), moreItem);
        moreList.setAdapter(adptr);
        moreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });
    }

    private void itemClick(int position) {
        switch (position) {
            case 0:
                dialogFagmentAddress address = new dialogFagmentAddress();
                address.show(getFragmentManager(), "dialog");
                break;
            case 1:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.trade.book.booktrade");
                startActivity(Intent.createChooser(share, "Share link!"));
                break;
            case 2:
                chooseHelpDialog();
                break;
            case 3:
                emailIntent();
                break;
            case 4:
                emailIntent();
                break;
            case 5:
                Intent settings = new Intent(getActivity(), PrefActivity.class);
                settings.putExtra(getResources().getString(R.string.intentExtraPrefrence), 3002);
                startActivity(settings);
                break;
            case 6:
                Intent about = new Intent(getActivity(), PrefActivity.class);
                about.putExtra(getResources().getString(R.string.intentExtraAbout), 3003);
                startActivity(about);
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
                    Toast.makeText(getActivity(), "Will show how to use", Toast.LENGTH_SHORT).show();
                }
                if (position == 1) {
                    emailIntent();
                }
            }
        });
        choosePath.create().show();
    }

    private void emailIntent() {
        Intent feedback = new Intent(Intent.ACTION_SENDTO);
        feedback.putExtra(Intent.EXTRA_EMAIL, new String[]{"shelf.bee.corp@gmail.com"});
        feedback.setData(Uri.parse("mailto:"));
        if (feedback.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(feedback);
        } else {
            Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

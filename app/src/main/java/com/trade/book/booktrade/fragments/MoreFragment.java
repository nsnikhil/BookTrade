package com.trade.book.booktrade.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.trade.book.booktrade.PrefActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;
import com.trade.book.booktrade.adapters.adapterList;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFagmentAddress;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentGetLocation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.LOCATION_SERVICE;


public class MoreFragment extends Fragment {

    @BindView(R.id.cateogoryList)
    ListView moreList;
    private Unbinder mUnbinder;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 56;

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
        moreItem.add("Recalibrate Location");
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
                checkLocation();
                break;
            case 2:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.trade.book.booktrade");
                startActivity(Intent.createChooser(share, "Share link!"));
                break;
            case 3:
                chooseHelpDialog();
                break;
            case 4:
                emailIntent();
                break;
            case 5:
                emailIntent();
                break;
            case 6:
                Intent settings = new Intent(getActivity(), PrefActivity.class);
                settings.putExtra(getResources().getString(R.string.intentExtraPrefrence), 3002);
                startActivity(settings);
                break;
            case 7:
                Intent about = new Intent(getActivity(), PrefActivity.class);
                about.putExtra(getResources().getString(R.string.intentExtraAbout), 3003);
                startActivity(about);
                break;
        }
    }

    private void checkLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                buildAlertMessageNoGps();
            }else {
                dialogFragmentGetLocation getLocation = new dialogFragmentGetLocation();
                getLocation.show(getFragmentManager(),"location");
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You should turn on gps to take advantage of all our services")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

package com.trade.book.booktrade.fragments.introfragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.trade.book.booktrade.R;


public class intro5Fragment extends Fragment implements ISlidePolicy {

    TextView locate;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 56;
    boolean location = false;


    public intro5Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro5, container, false);
        locate = (TextView) v.findViewById(R.id.intro5LocateText);
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            initilize();
        }else {
            askPermission();
        }
        return v;
    }

    private void askPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==MY_PERMISSIONS_ACCESS_COARSE_LOCATION){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                initilize();
            }
        }
    }

    private void initilize() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            if(mGoogleApiClient.isConnected()){
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    locate.setText("We have Located You");
                    Toast.makeText(getActivity(),String.valueOf(mLastLocation.getLatitude())+" "+String.valueOf(mLastLocation.getLongitude()),Toast.LENGTH_LONG).show();
                    location = true;
                    if(mGoogleApiClient!=null){
                        mGoogleApiClient.disconnect();
                    }
                }
            }
        }

    }


    @Override
    public boolean isPolicyRespected() {
        return location;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Toast.makeText(getActivity(),"We need you location to continue",Toast.LENGTH_SHORT).show();
        askPermission();
    }
}

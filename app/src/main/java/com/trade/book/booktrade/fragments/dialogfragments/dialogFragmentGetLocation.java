package com.trade.book.booktrade.fragments.dialogfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.book.booktrade.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class dialogFragmentGetLocation extends DialogFragment implements LocationListener {

    @BindView(R.id.locationLatitude)
    TextView mLatitude;
    @BindView(R.id.locationLongitude)
    TextView mLongitude;
    @BindView(R.id.locationStatus)
    TextView mStatus;
    private LocationManager locationManager;
    private String provider;
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_fragment_get_location, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("If your having trouble finding location try restarting the app if still ir dosen't work " +
                    "please close the app turn off the location and turn it back on and start the app again");
            mLatitude.setText("Location not available");
            mLongitude.setText("Location not available");
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }


    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        mLatitude.setText(String.valueOf(lat));
        mLongitude.setText(String.valueOf(lng));
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLatitude), String.valueOf(lat)).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLongitude), String.valueOf(lng)).apply();
        if(checkStatus(lat,lng)){
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("Hurrah are eligible to use all our services");
        }else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("We are sorry your region doesn't fall into our coverage zone, we are continuously working hard to expand our coverage zone");
        }
    }

    private boolean checkStatus(double myLatitude,double myLongitude) {
        double sLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.latitude));
        double sLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.longititude));
        float[] results = new float[1];
        Location.distanceBetween(sLatitude, sLongitude, myLatitude, myLongitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < 5000;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(), "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

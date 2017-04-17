package com.trade.book.booktrade.fragments.dialogfragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.trade.book.booktrade.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class dialogFragmentGetLocation extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    @BindView(R.id.locationLatitude)
    TextView mLatitude;
    @BindView(R.id.locationLongitude)
    TextView mLongitude;
    @BindView(R.id.locationStatus)
    TextView mStatus;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private Unbinder mUnbinder;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_fragment_get_location, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initialize();
        return v;
    }

    private void initialize() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    private void setLocationValue(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        mLatitude.setText(String.valueOf(lat));
        mLongitude.setText(String.valueOf(lng));
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLatitude), String.valueOf(lat)).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLongitude), String.valueOf(lng)).apply();
        if (checkStatus(lat, lng)||checkVelloreStatus(lat,lng)) {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("Hurrah!!! You are eligible to use all our services");
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("We are sorry your region doesn't fall into our coverage zone, we are continuously working hard to expand our coverage zone");
        }
    }


    private boolean checkStatus(double myLatitude, double myLongitude) {
        double sLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.latitude));
        double sLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.longititude));
        float[] results = new float[1];
        Location.distanceBetween(sLatitude, sLongitude, myLatitude, myLongitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < 5000;
    }

    private boolean checkVelloreStatus(double myLatitude, double myLongitude) {
        double vLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLatitude));
        double vLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLatitude));
        float[] results = new float[1];
        Location.distanceBetween(vLatitude, vLongitude, myLatitude, myLongitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < 5000;
    }


    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    public void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mStatus.setVisibility(View.GONE);
            setLocationValue(mLastLocation);
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText("If your having trouble finding location open your maps app locate yourself and then restart the app again");
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationValue(location);
    }
}

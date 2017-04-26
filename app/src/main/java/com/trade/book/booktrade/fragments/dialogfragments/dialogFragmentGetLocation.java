package com.trade.book.booktrade.fragments.dialogfragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private static final int mLocationPermissionCode = 56;
    @BindView(R.id.locationStatus)
    TextView mStatus;
    @BindView(R.id.locationStatusImage)
    ImageView mStatusImage;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private Unbinder mUnbinder;

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
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLatitude), String.valueOf(lat)).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(getResources().getString(R.string.prefLongitude), String.valueOf(lng)).apply();
        if (checkStatus(lat, lng) || checkVelloreStatus(lat, lng)) {
            mStatusImage.setVisibility(View.VISIBLE);
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText(getActivity().getResources().getString(R.string.locationServiceEligible));
        } else {
            mStatusImage.setVisibility(View.VISIBLE);
            mStatus.setVisibility(View.VISIBLE);
            mStatusImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.sad));
            mStatus.setText(getActivity().getResources().getString(R.string.locationServiceIllEligible));
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
        double vLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLongititude));
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, mLocationPermissionCode);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mStatus.setVisibility(View.GONE);
            setLocationValue(mLastLocation);
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText(getActivity().getResources().getString(R.string.locationErrorMessage));
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
        long UPDATE_INTERVAL = 10 * 1000;
        long FASTEST_INTERVAL = 2000;
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, mLocationPermissionCode);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationValue(location);
    }
}

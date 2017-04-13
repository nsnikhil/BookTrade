package com.trade.book.booktrade.fragments.introfragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.trade.book.booktrade.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class intro5Fragment extends Fragment implements ISlidePolicy/*, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {

    //GoogleApiClient mGoogleApiClient;
    //Location mLastLocation;
    //private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 56;
    //private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE_CODE = 57;
    private static final int mPermissionCode = 58;
    @BindView(R.id.intro5LocateText)
    TextView locate;
    @BindView(R.id.intro5GrantText)
    Button grant;
    boolean location = false;
    //int mCount = 1;
    private Unbinder mUnbinder;

    public intro5Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro5, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        //askPermission();
        //initilize();
        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        return v;
    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, mPermissionCode);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            location = true;
        }
    }

    /*private void askPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }*/

    /*@Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initilize() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }*/

    @Override
    public boolean isPolicyRespected() {
        return location;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        checkPermission();
        //Toast.makeText(getActivity(), "Wait while we locate you...", Toast.LENGTH_SHORT).show();
        //askPermission();
        //initilize();
        //doOnConnection();
    }

    /*private void doOnConnection(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locate.setText("Swipe left to locate");
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            getLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        doOnConnection();
    }

    private String locationUrl(String lat, String lang) {
        String main = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
        return main + lat + "," + lang + "&sensor=true";
    }

    private void getLocation(String lat, String lang) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, locationUrl(lat, lang), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    location = true;
                    setText(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        requestQueue.add(jsObjRequest);
    }

    private void setText(JSONObject object) throws JSONException {
        JSONArray array  = object.getJSONArray("results");
        JSONObject adr = array.getJSONObject(0);
        locate.setText("Your current location is\n"+adr.getString("formatted_address"));
        Toast.makeText(getActivity(),"Located",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

package com.trade.book.booktrade.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.trade.book.booktrade.AddBook;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterRequest;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentGetLocation;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.interfaces.interfaceAddRequest;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.RequestObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.LOCATION_SERVICE;

public class RequestListFragment extends Fragment implements interfaceAddRequest {

    private static final String mNullValue = "N/A";
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 56;
    @BindView(R.id.requestList)
    ListView requestList;
    @BindView(R.id.requestAdd)
    FloatingActionButton requestAdd;
    @BindView(R.id.requestListNoRequest)
    ImageView noRequest;
    @BindView(R.id.requestListSwipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    ArrayList<RequestObject> requestObjectList;
    adapterRequest adapter;
    Fragment f;
    private Unbinder mUnbinder;
    private boolean mTheme = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_request_list, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        intilize();
        mSwipeRefresh.setRefreshing(true);
        downloadList();
        f = this;
        checkPrefrence();
        return v;
    }

    private void checkPrefrence() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefValue = spf.getString(getResources().getString(R.string.prefThemeKey), mNullValue);
        if (prefValue.equalsIgnoreCase("Default")) {
            mTheme = false;
            noRequest.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.norequest));
        } else if (prefValue.equalsIgnoreCase("Multi-Color")) {
            mTheme = true;
            noRequest.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.norequestcolor));
        }
    }

    private void intilize() {
        requestObjectList = new ArrayList<>();
        requestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
                dialogFragmentRequest.setTargetFragment(f, 0);
                dialogFragmentRequest.show(getActivity().getSupportFragmentManager(), "request");
            }
        });
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder have = new AlertDialog.Builder(getActivity())
                        .setMessage("Do you want to sell this book")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Sell", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                if (spf.getString(getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue) ||
                                        spf.getString(getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
                                    checkLocation();
                                } else {
                                    if (checkStatus() || checkVelloreStatus()) {
                                        submitRequest(parent, position);
                                    } else {
                                        Toast.makeText(getActivity(), "We are sorry your region doesn't fall into " +
                                                "our coverage zone, we are continuously working hard to expand our coverage zone", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getActivity(), "If you think its a mistake try recalibrating location from more ", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });
                have.create().show();
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   requestList.setAdapter(null);
                                                   requestObjectList.clear();
                                                   downloadList();
                                               }
                                           }
        );
    }

    private void submitRequest(final AdapterView<?> parent, final int position) {
        RequestObject object = (RequestObject) parent.getItemAtPosition(position);
        Intent sell = new Intent(getActivity(), AddBook.class);
        sell.putExtra(getResources().getString(R.string.intentRequestBookName), object.getName());
        sell.putExtra(getResources().getString(R.string.intentRequestBookPublisher), object.getPublisher());
        sell.putExtra(getResources().getString(R.string.intentRequestBookRid), object.getRequestId());
        startActivity(sell);
    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        } else {
            LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                buildAlertMessageNoGps();
            } else {
                dialogFragmentGetLocation getLocation = new dialogFragmentGetLocation();
                getLocation.show(getFragmentManager(), "location");
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
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

    private boolean checkStatus() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double sLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.latitude));
        double sLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.longititude));
        double myLatitude = 0.0;
        double myLongitude = 0.0;
        int count = 0;
        if (!spf.getString(getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue)) {
            count++;
            myLatitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLatitude), mNullValue));
        }
        if (!spf.getString(getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
            count++;
            myLongitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLongitude), mNullValue));
        }
        if (count == 2) {
            float[] results = new float[1];
            Location.distanceBetween(sLatitude, sLongitude, myLatitude, myLongitude, results);
            float distanceInMeters = results[0];
            return distanceInMeters < 5000;
        }
        return false;
    }

    private boolean checkVelloreStatus() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double vLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLatitude));
        double vLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLongititude));
        double myLatitude = 0.0;
        double myLongitude = 0.0;
        int count = 0;
        if (!spf.getString(getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue)) {
            count++;
            myLatitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLatitude), mNullValue));
        }
        if (!spf.getString(getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
            count++;
            myLongitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLongitude), mNullValue));
        }
        if (count == 2) {
            float[] results = new float[1];
            Location.distanceBetween(vLatitude, vLongitude, myLatitude, myLongitude, results);
            float distanceInMeters = results[0];
            return distanceInMeters < 5000;
        }
        return false;
    }

    private void addToList(JSONArray array) throws JSONException {
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String uid = object.getString("id");
                String publisher = object.getString("publisher");
                int rid = object.getInt("rid");
                requestObjectList.add(new RequestObject(name, publisher, uid, rid));
            }
            noRequest.setVisibility(View.GONE);
            mSwipeRefresh.setRefreshing(false);
            adapter = new adapterRequest(getActivity(), requestObjectList);
            requestList.setAdapter(adapter);
        } else {
            mSwipeRefresh.setRefreshing(false);
            noRequest.setVisibility(View.VISIBLE);
        }
    }

    private void downloadList() {
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlRequestQuery);
        String url = host + queryFilename;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    addToList(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
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

    @Override
    public void refreshList() {
        mSwipeRefresh.setRefreshing(true);
        requestList.setAdapter(null);
        requestObjectList.clear();
        downloadList();
    }
}

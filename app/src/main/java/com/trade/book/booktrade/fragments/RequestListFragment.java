package com.trade.book.booktrade.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.RequestObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RequestListFragment extends Fragment {

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
    //View mainView = null;
    private Unbinder mUnbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_request_list, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        intilize();
        mSwipeRefresh.setRefreshing(true);
        downloadList();
        return v;
    }


    private void intilize() {
        requestObjectList = new ArrayList<>();
        requestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
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
                                RequestObject object = (RequestObject) parent.getItemAtPosition(position);
                                Intent sell = new Intent(getActivity(), AddBook.class);
                                sell.putExtra(getResources().getString(R.string.intentRequestBookName), object.getName());
                                sell.putExtra(getResources().getString(R.string.intentRequestBookPublisher), object.getPublisher());
                                sell.putExtra(getResources().getString(R.string.intentRequestBookRid), object.getRequestId());
                                startActivity(sell);
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
}

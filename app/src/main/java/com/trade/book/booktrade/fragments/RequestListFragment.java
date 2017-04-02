package com.trade.book.booktrade.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.AddBook;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.*;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.objects.RequestObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestListFragment extends Fragment {

    ListView requestList;
    FloatingActionButton requestAdd;
    ArrayList<RequestObject> requestObjectList;
    adapterRequest adapter;
    ImageView noRequest;
    SwipeRefreshLayout mSwipeRefresh;
    View mainView = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_request_list,container,false);
        intilize(mainView);
        mSwipeRefresh.setRefreshing(true);
        downloadList();
        return mainView;
    }

    private void intilize(View v) {
        requestAdd = (FloatingActionButton) v.findViewById(R.id.requestAdd);
        requestList = (ListView) v.findViewById(R.id.requestList);
        noRequest = (ImageView)v.findViewById(R.id.requestListNoRequest);
        mSwipeRefresh = (SwipeRefreshLayout)v.findViewById(R.id.requestListSwipeRefresh);
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
        }else {
            noRequest.setVisibility(View.VISIBLE);
        }
    }

    private void downloadList() {
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlRequestQuery);
        String url = host + queryFilename;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
        requestQueue.add(jsonArrayRequest);
    }
}

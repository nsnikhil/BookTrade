package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.trade.book.booktrade.adapters.*;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.objects.RequestObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestListActivity extends AppCompatActivity {

    ListView requestList;
    Toolbar requestToolbar;
    FloatingActionButton requestAdd;
    ArrayList<RequestObject> requestObjectList;
    adapterRequest adapter;
    ImageView noRequest;
    SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        intilize();
        mSwipeRefresh.setRefreshing(true);
        downloadList();
    }

    private void intilize() {
        requestAdd = (FloatingActionButton) findViewById(R.id.requestAdd);
        requestToolbar = (Toolbar) findViewById(R.id.toolbarRequest);
        setSupportActionBar(requestToolbar);
        getSupportActionBar().setTitle("Book Request");
        requestList = (ListView) findViewById(R.id.requestList);
        noRequest = (ImageView)findViewById(R.id.requestListNoRequest);
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.requestListSwipeRefresh);
        requestObjectList = new ArrayList<>();
        requestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
                dialogFragmentRequest.show(getSupportFragmentManager(), "request");
            }
        });
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder have = new AlertDialog.Builder(RequestListActivity.this)
                        .setMessage("Do you want to sell this book")
                        .setPositiveButton("Sell", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RequestObject object = (RequestObject) parent.getItemAtPosition(position);
                                Intent sell = new Intent(RequestListActivity.this, AddBook.class);
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
            adapter = new adapterRequest(getApplicationContext(), requestObjectList);
            requestList.setAdapter(adapter);
        }else {
            noRequest.setVisibility(View.VISIBLE);
        }
    }

    private void downloadList() {
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlRequestQuery);
        String url = host + queryFilename;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}

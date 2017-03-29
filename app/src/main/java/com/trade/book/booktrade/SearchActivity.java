package com.trade.book.booktrade;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.adapters.adapterBookList;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    GridView searchList;
    ImageView noList;
    ProgressBar searchProgress;
    ArrayList<BookObject> srchList;
    com.lapism.searchview.SearchView searchView;
    FloatingActionButton request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initilize();
        search();

    }

    private void initilize() {
        searchList = (GridView) findViewById(R.id.searchListGrid);
        srchList = new ArrayList<>();
        noList = (ImageView) findViewById(R.id.searchErrorImage);
        searchProgress = (ProgressBar) findViewById(R.id.searchProgress);
        searchView = (com.lapism.searchview.SearchView)findViewById(R.id.sSearchView);
        searchView.setHint(getResources().getString(R.string.searchHint));
        request = (FloatingActionButton)findViewById(R.id.searchRequest);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
                dialogFragmentRequest.show(getSupportFragmentManager(), "request");
            }
        });
        searchView.setElevation(8);
        searchView.setOnVoiceClickListener(new com.lapism.searchview.SearchView.OnVoiceClickListener() {
            @Override
            public void onVoiceClick() {
                Toast.makeText(getApplicationContext(),"Will Start Voice Search",Toast.LENGTH_LONG).show();
            }
        });
        searchView.setOnQueryTextListener(new com.lapism.searchview.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProgress.setVisibility(View.VISIBLE);
                noList.setVisibility(View.GONE);
                request.setVisibility(View.GONE);
                inSearch(query);
                searchView.close(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                Intent detail  = new Intent(SearchActivity.this,PurchaseActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                detail.putExtras(b);
                startActivity(detail);
            }
        });
    }

    private void search() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(getResources().getString(R.string.intenSearchKey));
            buildRequest(query);
        }
    }

    private void inSearch(String query){
        searchList.setAdapter(null);
        srchList.clear();
        buildRequest(query);
    }

    private String searchQuery(String query) {
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlSearchQuery);
        String url = host + searchFileName;
        String searchQuery = "nm";
        String searchValue = query;
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(searchQuery, searchValue)
                .build()
                .toString();
    }

    private void buildRequest(String query) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, searchQuery(query), null, new Response.Listener<JSONArray>() {
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
                noList.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void addToList(JSONArray response) throws JSONException {
        searchProgress.setVisibility(View.GONE);
        if (response.length() > 0) {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                int bid = jsonObject.getInt("id");
                String name = jsonObject.getString("Name");
                String publisher = jsonObject.getString("Publisher");
                int costPrice = jsonObject.getInt("CostPrice");
                int sellingPrice = jsonObject.getInt("SellingPrice");
                int edition = jsonObject.getInt("Edition");
                String description = jsonObject.getString("Description");
                String condtn = jsonObject.getString("Cndtn");
                String cateogory = jsonObject.getString("Cateogory");
                String userId = jsonObject.getString("userId");
                int itmId = jsonObject.getInt("id");
                String photoUrlName0 = jsonObject.getString("pic0");
                String photoUrlName1 = jsonObject.getString("pic1");
                String photoUrlName2 = jsonObject.getString("pic2");
                String photoUrlName3 = jsonObject.getString("pic3");
                String photoUrlName4 = jsonObject.getString("pic4");
                String photoUrlName5 = jsonObject.getString("pic5");
                String photoUrlName6 = jsonObject.getString("pic6");
                String photoUrlName7 = jsonObject.getString("pic7");
                int status = jsonObject.getInt("status");
                srchList.add(new BookObject(bid,name, publisher, costPrice, sellingPrice, edition, description, condtn, cateogory, userId,itmId
                        ,photoUrlName0,photoUrlName1,photoUrlName2,photoUrlName3,photoUrlName4,photoUrlName5,photoUrlName6,photoUrlName7,status));
            }
            adapterBookList bookAdapter = new adapterBookList(getApplicationContext(), srchList,0);
            searchList.setAdapter(bookAdapter);
        }else {
            noList.setVisibility(View.VISIBLE);
            request.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"You can add book request clicking the  + icon",Toast.LENGTH_LONG).show();
        }
    }

}

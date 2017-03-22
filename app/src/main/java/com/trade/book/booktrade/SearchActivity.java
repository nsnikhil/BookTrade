package com.trade.book.booktrade;

import android.content.Intent;
import android.net.Uri;
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
                String name = jsonObject.getString("Name");
                String publisher = jsonObject.getString("Publisher");
                int costPrice = jsonObject.getInt("CostPrice");
                int sellingPrice = jsonObject.getInt("SellingPrice");
                int edition = jsonObject.getInt("Edition");
                String description = jsonObject.getString("Description");
                String condtn = jsonObject.getString("Cndtn");
                String cateogory = jsonObject.getString("Cateogory");
                int userId = jsonObject.getInt("userId");
                int itmId = jsonObject.getInt("id");
                srchList.add(new BookObject(name, publisher, costPrice, sellingPrice, edition, description, condtn, cateogory, userId,itmId));
            }
            adapterBookList bookAdapter = new adapterBookList(getApplicationContext(), srchList);
            searchList.setAdapter(bookAdapter);
        }else {
            noList.setVisibility(View.VISIBLE);
        }
    }

}

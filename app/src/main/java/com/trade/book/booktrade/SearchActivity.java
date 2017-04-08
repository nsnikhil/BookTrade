package com.trade.book.booktrade;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
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
import com.lapism.searchview.SearchView;
import com.squareup.leakcanary.LeakCanary;
import com.trade.book.booktrade.adapters.adapterBookList;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.searchListGrid) GridView searchList;
    @BindView(R.id.searchErrorImage) ImageView noList;
    @BindView(R.id.searchProgress) ProgressBar searchProgress;
    @BindView(R.id.sSearchView) SearchView searchView;
    @BindView(R.id.searchRequest) FloatingActionButton request;
    ArrayList<BookObject> srchList;
    private static final int  REQ_CODE_SPEECH_INPUT = 564;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        if(!checkConnection()){
            AlertDialog.Builder noInternet = new AlertDialog.Builder(SearchActivity.this);
            noInternet.setMessage("No Internet").setCancelable(false).create().show();
        }
        initilize();
        search();
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void initilize() {
        srchList = new ArrayList<>();
        searchView.setHint(getResources().getString(R.string.searchHint));
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
                dialogFragmentRequest.show(getSupportFragmentManager(), "request");
            }
        });
        searchView.setElevation(8);
        searchView.setOnVoiceClickListener(new SearchView.OnVoiceClickListener() {
            @Override
            public void onVoiceClick() {
                promptSpeechInput();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> p1 = Pair.create(view, "transitionBookName");
                    Pair<View, String> p2 = Pair.create(view, "transitionBookPublisher");
                    Pair<View, String> p3 = Pair.create(view, "transitionBookImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this, p1, p2, p3);
                    startActivity(detail,options.toBundle());
                }else {
                    startActivity(detail);
                }
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && null != data) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchView.setTextOnly(result.get(0));
            searchProgress.setVisibility(View.VISIBLE);
            noList.setVisibility(View.GONE);
            request.setVisibility(View.GONE);
            inSearch(result.get(0));
            searchView.close(true);
        }
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

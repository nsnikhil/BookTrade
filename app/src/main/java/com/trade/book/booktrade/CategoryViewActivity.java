package com.trade.book.booktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class CategoryViewActivity extends AppCompatActivity {

    GridView catList;
    ImageView emptyCat;
    Toolbar catToolbar;
    ProgressBar catProgress;
    ArrayList<BookObject> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);
        initilize();
        String catUri = getIntent().getExtras().getString(getResources().getString(R.string.intencateuri));
        getList(catUri);
    }

    private void initilize() {
        catList = (GridView)findViewById(R.id.catrLst);
        emptyCat = (ImageView)findViewById(R.id.categoryEmpty);
        catToolbar = (Toolbar)findViewById(R.id.toolbarCategory);
        setSupportActionBar(catToolbar);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.bookCateogories)[ getIntent().getExtras().getInt(getResources().getString(R.string.intencateuripos))]);
        catProgress = (ProgressBar)findViewById(R.id.cateogoryProgress);
        catProgress.setVisibility(View.VISIBLE);
        categoryList = new ArrayList<>();
        catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                Intent detail  = new Intent(getApplicationContext(),PurchaseActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                detail.putExtras(b);
                startActivity(detail);
            }
        });
    }

    private void getList(String url){
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
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void addToList(JSONArray response) throws JSONException {
        catProgress.setVisibility(View.GONE);
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
                int itemId = jsonObject.getInt("id");
                String photoUrlName0 = jsonObject.getString("pic0");
                String photoUrlName1 = jsonObject.getString("pic1");
                String photoUrlName2 = jsonObject.getString("pic2");
                String photoUrlName3 = jsonObject.getString("pic3");
                String photoUrlName4 = jsonObject.getString("pic4");
                String photoUrlName5 = jsonObject.getString("pic5");
                String photoUrlName6 = jsonObject.getString("pic6");
                String photoUrlName7 = jsonObject.getString("pic7");
                categoryList.add(new BookObject(name, publisher, costPrice, sellingPrice, edition, description, condtn, cateogory, userId, itemId
                        ,photoUrlName0,photoUrlName1,photoUrlName2,photoUrlName3,photoUrlName4,photoUrlName5,photoUrlName6,photoUrlName7));
            }
            adapterBookList bookAdapter = new adapterBookList(getApplicationContext(), categoryList);
            catList.setAdapter(bookAdapter);
        }else {
            emptyCat.setVisibility(View.VISIBLE);
        }
    }
}

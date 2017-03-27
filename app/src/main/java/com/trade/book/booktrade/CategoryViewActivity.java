package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.toolbox.StringRequest;
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
    int mUploadIndicator = 0;
    private static final int mRequestCode = 151;
    String catUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);
        catUri = getIntent().getExtras().getString(getResources().getString(R.string.intencateuri));
        mUploadIndicator = getIntent().getExtras().getInt(getResources().getString(R.string.intenupind));
        initilize();
        getList(catUri);
    }

    private void initilize() {
        catList = (GridView)findViewById(R.id.catrLst);
        emptyCat = (ImageView)findViewById(R.id.categoryEmpty);
        catToolbar = (Toolbar)findViewById(R.id.toolbarCategory);
        setSupportActionBar(catToolbar);
        if(mUploadIndicator==0){
            getSupportActionBar().setTitle(getResources().getStringArray(R.array.bookCateogories)[ getIntent().getExtras().getInt(getResources().getString(R.string.intencateuripos))]);
        }else if(mUploadIndicator==123){
            getSupportActionBar().setTitle("My Purchases");
        } else {
            getSupportActionBar().setTitle("My Uploads");
        }
        catProgress = (ProgressBar)findViewById(R.id.cateogoryProgress);
        catProgress.setVisibility(View.VISIBLE);
        categoryList = new ArrayList<>();
        catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mUploadIndicator!=123) {
                    BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                    Intent detail = new Intent(getApplicationContext(), PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    if (mUploadIndicator != 0) {
                        detail.putExtra(getResources().getString(R.string.intentfromupload), 123);
                    }
                    startActivityForResult(detail, mRequestCode);
                }else {
                    final BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                    AlertDialog.Builder cancel = new AlertDialog.Builder(CategoryViewActivity.this)
                            .setTitle("Warning")
                            .setMessage("Do you want to cancel your purchase")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  int bookId = bookObject.getBid();
                                    cancelPurchase(bookId);
                                    moveToAvailable(bookId);
                                }
                            });
                    cancel.create().show();
                }
            }
        });
    }

    private String buildCancelPurchaseUri(int bid){
        String host = getResources().getString(R.string.urlServer);
        String cancelPurchase = getResources().getString(R.string.urlTransactionDelete);
        String url = host+cancelPurchase;
        String bidQuery = "bkid";
        String bidValue  = String.valueOf(bid);
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery,bidValue).build().toString();
    }

    private void cancelPurchase(int bid){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildCancelPurchaseUri(bid), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private String buildMoveToAvailable(int bid){
        String host = getResources().getString(R.string.urlServer);
        String cancelPurchase = getResources().getString(R.string.urlMoveToAvailable);
        String url = host+cancelPurchase;
        String bidQuery = "id";
        String bidValue  = String.valueOf(bid);
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery,bidValue).build().toString();
    }

    private void moveToAvailable(int bid){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildMoveToAvailable(bid), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(CategoryViewActivity.this,MainActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==mRequestCode){
            if(resultCode==RESULT_OK){
                catList.setAdapter(null);
                categoryList.clear();
                getList(catUri);
            }
        }
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
                int itemId = jsonObject.getInt("id");
                String photoUrlName0 = jsonObject.getString("pic0");
                String photoUrlName1 = jsonObject.getString("pic1");
                String photoUrlName2 = jsonObject.getString("pic2");
                String photoUrlName3 = jsonObject.getString("pic3");
                String photoUrlName4 = jsonObject.getString("pic4");
                String photoUrlName5 = jsonObject.getString("pic5");
                String photoUrlName6 = jsonObject.getString("pic6");
                String photoUrlName7 = jsonObject.getString("pic7");
                categoryList.add(new BookObject(bid,name, publisher, costPrice, sellingPrice, edition, description, condtn, cateogory, userId, itemId
                        ,photoUrlName0,photoUrlName1,photoUrlName2,photoUrlName3,photoUrlName4,photoUrlName5,photoUrlName6,photoUrlName7));
            }
            adapterBookList bookAdapter = new adapterBookList(getApplicationContext(), categoryList);
            catList.setAdapter(bookAdapter);
        }else {
            emptyCat.setVisibility(View.VISIBLE);
        }
    }
}

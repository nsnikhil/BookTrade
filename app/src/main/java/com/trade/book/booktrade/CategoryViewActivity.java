package com.trade.book.booktrade;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.trade.book.booktrade.adapters.adapterBookList;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryViewActivity extends AppCompatActivity {

    private static final int mRequestCode = 151;
    @BindView(R.id.catrLst)
    GridView catList;
    @BindView(R.id.categoryEmpty)
    ImageView emptyCat;
    @BindView(R.id.toolbarCategory)
    Toolbar catToolbar;
    ArrayList<BookObject> categoryList;
    int mUploadIndicator = 0;
    @BindView(R.id.catListSwipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    String catUri;
    AlertDialog.Builder mAlertDialog;
    Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        setContentView(R.layout.activity_category_view);
        ButterKnife.bind(this);
        catUri = getIntent().getExtras().getString(getResources().getString(R.string.intencateuri));
        mUploadIndicator = getIntent().getExtras().getInt(getResources().getString(R.string.intenupind));
        initilize();
        mSwipeRefresh.setRefreshing(true);
        if (mUploadIndicator == 123) {
            getList(catUri, 1);
        } else {
            getList(catUri, 0);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initilize() {
        setSupportActionBar(catToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mUploadIndicator == 0) {
            getSupportActionBar().setTitle(getIntent().getExtras().getString(getResources().getString(R.string.intencateuripos)));
        } else if (mUploadIndicator == 123) {
            getSupportActionBar().setTitle("My Purchases");
        } else {
            getSupportActionBar().setTitle("My Uploads");
        }
        categoryList = new ArrayList<>();
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   catList.setAdapter(null);
                                                   categoryList.clear();
                                                   if (mUploadIndicator == 123) {
                                                       getList(catUri, 1);
                                                   } else {
                                                       getList(catUri, 0);
                                                   }
                                               }
                                           }
        );
        catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUploadIndicator != 123) {
                    BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                    Intent detail = new Intent(getApplicationContext(), PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    if (mUploadIndicator != 0) {
                        detail.putExtra(getResources().getString(R.string.intentfromupload), 123);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create(view, "transitionBookName");
                        Pair<View, String> p2 = Pair.create(view, "transitionBookPublisher");
                        Pair<View, String> p3 = Pair.create(view, "transitionBookImage");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CategoryViewActivity.this, p1, p2, p3);
                        startActivityForResult(detail, mRequestCode, options.toBundle());
                    } else {
                        startActivityForResult(detail, mRequestCode);
                    }
                } else {
                    mAlertDialog = new AlertDialog.Builder(CategoryViewActivity.this);
                    mAlertDialog.setMessage(getResources().getString(R.string.myLoading));
                    mAlertDialog.setCancelable(false);
                    mDialog = mAlertDialog.create();
                    mDialog.show();
                    BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                    checkForCancel(bookObject);
                }
            }
        });
    }

    private void checkIfCancelable(JSONArray array, BookObject object) throws JSONException {
        if (array.length() > 0) {
            JSONObject jsonObject = array.getJSONObject(0);
            String time = jsonObject.getString("buytime");
            String status = jsonObject.getString("tranStatus");
            if (Integer.parseInt(status) == 0) {
                try {
                    formatDate(time, object);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                mDialog.dismiss();
                mDialog = null;
                mAlertDialog = null;
                AlertDialog.Builder delivered = new AlertDialog.Builder(CategoryViewActivity.this);
                delivered.setMessage("Book was delivered successfully ")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        }
    }

    private void formatDate(String dateString, final BookObject object) throws ParseException {
        Calendar c = Calendar.getInstance();
        long seconds = Long.parseLong(dateString);
        long first = TimeUnit.MILLISECONDS.toDays(seconds);
        long second = TimeUnit.MILLISECONDS.toDays(c.getTimeInMillis());
        if (second - first > 3) {
            mDialog.dismiss();
            mDialog = null;
            mAlertDialog = null;
            AlertDialog.Builder delivered = new AlertDialog.Builder(CategoryViewActivity.this);
            delivered.setMessage("You cannot cancel your purchase now")
                    .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        } else {
            mDialog.dismiss();
            mDialog = null;
            mAlertDialog = null;
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
                            int bookId = object.getBid();
                            cancelPurchase(bookId);
                            moveToAvailable(bookId);
                        }
                    });
            cancel.create().show();
        }
    }

    private String buildCheckCancel(int bid) {
        String host = getResources().getString(R.string.urlServer);
        String cancelPurchase = getResources().getString(R.string.urlTransactionCheck);
        String url = host + cancelPurchase;
        String bidQuery = "bkid";
        String bidValue = String.valueOf(bid);
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery, bidValue).build().toString();
    }

    private void checkForCancel(final BookObject bookObject) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildCheckCancel(bookObject.getBid()), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    checkIfCancelable(response, bookObject);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }


    private String buildCancelPurchaseUri(int bid) {
        String host = getResources().getString(R.string.urlServer);
        String cancelPurchase = getResources().getString(R.string.urlTransactionDelete);
        String url = host + cancelPurchase;
        String bidQuery = "bkid";
        String bidValue = String.valueOf(bid);
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery, bidValue).build().toString();
    }

    private void cancelPurchase(int bid) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildCancelPurchaseUri(bid), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private String buildMoveToAvailable(int bid) {
        String host = getResources().getString(R.string.urlServer);
        String cancelPurchase = getResources().getString(R.string.urlMoveToAvailable);
        String url = host + cancelPurchase;
        String bidQuery = "id";
        String bidValue = String.valueOf(bid);
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery, bidValue).build().toString();
    }

    private void moveToAvailable(int bid) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildMoveToAvailable(bid), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(CategoryViewActivity.this, StartActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode) {
            if (resultCode == RESULT_OK) {
                catList.setAdapter(null);
                categoryList.clear();
                getList(catUri, 0);
            }
        }
    }

    private void getList(String url, final int k) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    addToList(response, k);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void addToList(JSONArray response, int k) throws JSONException {
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
                int status = jsonObject.getInt("status");
                categoryList.add(new BookObject(bid, name, publisher, costPrice, sellingPrice, edition, description, condtn, cateogory, userId, itemId
                        , photoUrlName0, photoUrlName1, photoUrlName2, photoUrlName3, photoUrlName4, photoUrlName5, photoUrlName6, photoUrlName7, status));
            }
            mSwipeRefresh.setRefreshing(false);
            adapterBookList bookAdapter = new adapterBookList(getApplicationContext(), categoryList, k);
            catList.setAdapter(bookAdapter);
        } else {
            mSwipeRefresh.setRefreshing(false);
            emptyCat.setVisibility(View.VISIBLE);
        }
    }
}

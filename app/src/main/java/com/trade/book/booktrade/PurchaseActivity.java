package com.trade.book.booktrade;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.trade.book.booktrade.adapters.adapterPurchaseImage;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentGetLocation;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentPurchase;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String mNullValue = "N/A";
    private static final int mEditRequestCode = 584;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 56;
    @BindView(R.id.purchaseName)
    TextView name;
    @BindView(R.id.purchasePublisher)
    TextView publisher;
    @BindView(R.id.purchaseEdition)
    TextView edition;
    @BindView(R.id.purchaseDescription)
    TextView description;
    @BindView(R.id.purchaseCategory)
    TextView cateogory;
    @BindView(R.id.purchaseCondition)
    TextView condition;
    @BindView(R.id.purchaseErrorText)
    TextView purchaseError;
    @BindView(R.id.purchaseBuy)
    Button buyNow;
    @BindView(R.id.purchaseAddTocart)
    Button addToCart;
    @BindView(R.id.purchaseButtonContainer)
    LinearLayout buttonConatiner;
    @BindView(R.id.purchaseBookView)
    RecyclerView imageHolder;
    @BindView(R.id.purchaseToolbar)
    Toolbar mPurchaseToolbar;
    BookObject bObject = null;
    adapterPurchaseImage imageAdapter;
    ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        setContentView(R.layout.activity_purchase);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initilize();
        if (getIntent().getExtras() != null) {
            bObject = (BookObject) getIntent().getExtras().getSerializable(getResources().getString(R.string.intenKeyObejct));
            setValue(bObject);
            SharedPreferences sfp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (bObject.getUserId().equalsIgnoreCase(sfp.getString(getResources().getString(R.string.prefAccountId), mNullValue))) {
                purchaseError.setText(getResources().getString(R.string.purchaseCannotBuyYourOwnBook));
                purchaseError.setVisibility(View.VISIBLE);
                buttonConatiner.setVisibility(View.GONE);
            } else {
                purchaseError.setVisibility(View.GONE);
                buttonConatiner.setVisibility(View.VISIBLE);
            }
        }
        setCartText();
        getUrl();
        if (getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload)) != 0) {
            if (bObject.getStatus() == 1) {
                purchaseError.setText(getResources().getString(R.string.purchaseCannotModifySoldBooks));
                purchaseError.setVisibility(View.VISIBLE);
                buttonConatiner.setVisibility(View.GONE);
            } else {
                purchaseError.setVisibility(View.GONE);
                buttonConatiner.setVisibility(View.VISIBLE);
                buyNow.setText(getResources().getString(R.string.purchaseEdit));
                addToCart.setText(getResources().getString(R.string.purchaseDelete));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initilize() {
        setSupportActionBar(mPurchaseToolbar);
        mPurchaseToolbar.setPadding(0, 52, 0, 0);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fill_transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageHolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        addToCart.setOnClickListener(this);
        buyNow.setOnClickListener(this);
    }

    private void setValue(BookObject bookObject) {
        name.setText(bookObject.getName());
        publisher.setText(bookObject.getPublisher());
        edition.setText(getResources().getString(R.string.bookEdition) + " : " + bookObject.getEdition());
        if (bookObject.getDescription().equalsIgnoreCase("") || bookObject.getDescription().isEmpty()) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(getResources().getString(R.string.purchaseDescription) + " : " + bookObject.getDescription());
        }
        cateogory.setText(getResources().getString(R.string.bookCateogory) + " : " + bookObject.getCateogory());
        condition.setText(getResources().getString(R.string.bookCondition) + " : " + bookObject.getCondition());
        imageHolder.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color));
        buyNow.setText(" Buy Now " + " र " + bookObject.getSellingPrice());
    }

    private void getUrl() {
        urls = new ArrayList<>();
        String baseUrl = getResources().getString(R.string.urlBucetHost) + getResources().getString(R.string.urlBucketName);
        if (!bObject.getPhoto0().equalsIgnoreCase("null") && bObject.getPhoto0() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto0());
        }
        if (!bObject.getPhoto1().equalsIgnoreCase("null") && bObject.getPhoto1() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto1());
        }
        if (!bObject.getPhoto2().equalsIgnoreCase("null") && bObject.getPhoto2() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto2());
        }
        if (!bObject.getPhoto3().equalsIgnoreCase("null") && bObject.getPhoto3() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto3());
        }
        if (!bObject.getPhoto4().equalsIgnoreCase("null") && bObject.getPhoto4() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto4());
        }
        if (!bObject.getPhoto5().equalsIgnoreCase("null") && bObject.getPhoto5() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto5());
        }
        if (!bObject.getPhoto6().equalsIgnoreCase("null") && bObject.getPhoto6() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto6());
        }
        if (!bObject.getPhoto7().equalsIgnoreCase("null") && bObject.getPhoto7() != null) {
            urls.add(baseUrl + "/" + bObject.getPhoto7());
        }
        imageAdapter = new adapterPurchaseImage(PurchaseActivity.this, urls, 0);
        imageHolder.setAdapter(imageAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.purchaseAddTocart:
                if (getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload)) != 0) {
                    AlertDialog.Builder delete = new AlertDialog.Builder(PurchaseActivity.this);
                    delete.setTitle("Warning").setMessage("Are you sure yo want to delete this book")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItem();
                        }
                    });
                    delete.create().show();
                } else {
                    checkInCart(bObject);
                }
                break;
            case R.id.purchaseBuy:
                if (getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload)) != 0) {
                    Intent detail = new Intent(getApplicationContext(), AddBook.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intentEditObject), bObject);
                    detail.putExtras(b);
                    startActivityForResult(detail, mEditRequestCode);
                } else {
                    SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (spf.getString(getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue) ||
                            spf.getString(getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
                        checkLocation();
                    } else {
                        if (checkStatus() || checkVelloreStatus()) {
                            checkSold(bObject.getBid());
                        } else {
                            Toast.makeText(getApplicationContext(), "We are sorry your region doesn't fall into " +
                                    "our coverage zone, we are continuously working hard to expand our coverage zone", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "If you think its a mistake try recalibrating location from more ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(PurchaseActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        } else {
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                buildAlertMessageNoGps();
            } else {
                dialogFragmentGetLocation getLocation = new dialogFragmentGetLocation();
                getLocation.show(getSupportFragmentManager(), "location");
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double sLatitude = Double.parseDouble(getApplicationContext().getResources().getString(R.string.latitude));
        double sLongitude = Double.parseDouble(getApplicationContext().getResources().getString(R.string.longititude));
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
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double vLatitude = Double.parseDouble(getApplicationContext().getResources().getString(R.string.velloreLatitude));
        double vLongitude = Double.parseDouble(getApplicationContext().getResources().getString(R.string.velloreLongititude));
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

    private void showPurchaseDialog() {
        dialogFragmentPurchase dialogFragmentPurchase = new dialogFragmentPurchase();
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.bundleBookName), name.getText().toString());
        args.putString(getResources().getString(R.string.bundleBookPublisher), publisher.getText().toString());
        args.putInt(getResources().getString(R.string.bundleBookPrice), bObject.getSellingPrice());
        args.putString(getResources().getString(R.string.bundleBookSellerUid), bObject.getUserId());
        args.putInt(getResources().getString(R.string.bundleBookBookId), bObject.getBid());
        dialogFragmentPurchase.setArguments(args);
        dialogFragmentPurchase.show(getSupportFragmentManager(), "purchase");
    }

    private String buildSoldUri(int id) {
        String host = getResources().getString(R.string.urlServer);
        String purchaseAvailable = getResources().getString(R.string.urlPurchaseAvailable);
        String url = host + purchaseAvailable;
        String bidQuery = "bd";
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bidQuery, String.valueOf(id))
                .build().toString();
    }

    private void checkSold(int bid) {
        AlertDialog.Builder wait = new AlertDialog.Builder(PurchaseActivity.this);
        wait.setMessage("Verifying");
        final Dialog dw = wait.create();
        dw.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildSoldUri(bid), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    showIfAvailable(response, dw);
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

    private void showIfAvailable(JSONArray status, Dialog d) throws JSONException {
        if (status.length() > 0) {
            JSONObject obj = status.getJSONObject(0);
            int statusCode = obj.getInt("status");
            d.dismiss();
            if (statusCode == 1) {
                Toast.makeText(getApplicationContext(), "Book sold", Toast.LENGTH_LONG).show();
            } else {
                showPurchaseDialog();
            }
        }
    }

    private String buildDeleteUri() {
        String host = getResources().getString(R.string.urlServer);
        String deleteBook = getResources().getString(R.string.urlDeleteSingle);
        String url = host + deleteBook;
        String bidQuery = "id";
        int bidValue = bObject.getBid();
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery, String.valueOf(bidValue)).build().toString();
    }

    private void deleteItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildDeleteUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(PurchaseActivity.this, StartActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void removeBook() {
        if (bObject != null) {
            int count = getContentResolver().delete(Uri.withAppendedPath(CartTables.mCartContentUri, String.valueOf(bObject.getItemId())), null, null);
            if (count == 0) {
                Toast.makeText(getApplicationContext(), "Error while removing item", Toast.LENGTH_SHORT).show();
            } else {
                setCartText();
                Toast.makeText(getApplicationContext(), "Item Removed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkInCart(BookObject bObject) {
        Cursor c = getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        if (c.getCount() == 0) {
            insertVal();
        } else {
            while (c.moveToNext()) {
                if (c.getInt(c.getColumnIndex(tablecart.mUid)) == bObject.getItemId()) {
                    removeBook();
                    return;
                }
            }
            insertVal();
        }
    }

    private void setCartText() {
        Cursor c = getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        if (c.getCount() == 0) {
            addToCart.setText(getResources().getString(R.string.purchaseAddToCart));
        }
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex(tablecart.mUid)) == bObject.getItemId()) {
                addToCart.setText(getResources().getString(R.string.purchaseRemoveFromCart));
                return;
            } else {
                addToCart.setText(getResources().getString(R.string.purchaseAddToCart));
            }
        }
    }

    private void insertVal() {
        ContentValues cv = new ContentValues();
        cv.put(tablecart.mBuid, bObject.getBid());
        cv.put(tablecart.mUid, bObject.getItemId());
        cv.put(tablecart.mName, bObject.getName());
        cv.put(tablecart.mPublisher, bObject.getPublisher());
        cv.put(tablecart.mCostPrice, bObject.getCostPrice());
        cv.put(tablecart.mSellingPrice, bObject.getSellingPrice());
        cv.put(tablecart.mEdition, bObject.getEdition());
        cv.put(tablecart.mCondition, bObject.getCondition());
        cv.put(tablecart.mCateogory, bObject.getCateogory());
        cv.put(tablecart.mDescription, bObject.getDescription());
        cv.put(tablecart.mUserId, bObject.getUserId());
        cv.put(tablecart.mPhoto0, bObject.getPhoto0());
        cv.put(tablecart.mPhoto1, bObject.getPhoto1());
        cv.put(tablecart.mPhoto2, bObject.getPhoto2());
        cv.put(tablecart.mPhoto3, bObject.getPhoto3());
        cv.put(tablecart.mPhoto4, bObject.getPhoto4());
        cv.put(tablecart.mPhoto5, bObject.getPhoto5());
        cv.put(tablecart.mPhoto6, bObject.getPhoto6());
        cv.put(tablecart.mPhoto7, bObject.getPhoto7());
        cv.put(tablecart.mstatus, bObject.getStatus());
        getContentResolver().insert(CartTables.mCartContentUri, cv);
        Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_SHORT).show();
        setCartText();
    }
}

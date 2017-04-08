package com.trade.book.booktrade;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.adapters.*;
import com.trade.book.booktrade.fragments.dialogfragments.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener{

    TextView name,publisher,edition,description,cateogory,condition,purchaseError;
    Button buyNow,addToCart;
    LinearLayout buttonConatiner;
    RecyclerView imageHolder;
    BookObject bObject = null;
    adapterPurchaseImage imageAdapter;
    ArrayList<String> urls;
    private static final String mNullValue = "N/A";
    private static final int mEditRequestCode = 584;


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
        if(getIntent().getExtras()!=null){
            bObject = (BookObject) getIntent().getExtras().getSerializable(getResources().getString(R.string.intenKeyObejct));
            setValue(bObject);
            SharedPreferences sfp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(bObject.getUserId().equalsIgnoreCase(sfp.getString(getResources().getString(R.string.prefAccountId),mNullValue))){
                purchaseError.setText(getResources().getString(R.string.purchaseCannotBuyYourOwnBook));
                purchaseError.setVisibility(View.VISIBLE);
                buttonConatiner.setVisibility(View.GONE);
            }else {
                purchaseError.setVisibility(View.GONE);
                buttonConatiner.setVisibility(View.VISIBLE);
            }
        }
        setCartText();
        getUrl();
        if(getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload))!=0){
            if(bObject.getStatus()==1){
                purchaseError.setText(getResources().getString(R.string.purchaseCannotModifySoldBooks));
                purchaseError.setVisibility(View.VISIBLE);
                buttonConatiner.setVisibility(View.GONE);
            }else {
                purchaseError.setVisibility(View.GONE);
                buttonConatiner.setVisibility(View.VISIBLE);
                buyNow.setText(getResources().getString(R.string.purchaseEdit));
                addToCart.setText(getResources().getString(R.string.purchaseDelete));
            }
        }
    }

    private void initilize() {
        name = (TextView)findViewById(R.id.purchaseName);
        publisher = (TextView)findViewById(R.id.purchasePublisher);
        edition = (TextView)findViewById(R.id.purchaseEdition);
        description = (TextView)findViewById(R.id.purchaseDescription);
        cateogory = (TextView)findViewById(R.id.purchaseCategory);
        condition = (TextView)findViewById(R.id.purchaseCondition);
        buyNow = (Button)findViewById(R.id.purchaseBuy);

        imageHolder = (RecyclerView)findViewById(R.id.purchaseBookView);
        imageHolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        addToCart = (Button)findViewById(R.id.purchaseAddTocart);
        addToCart.setOnClickListener(this);
        buyNow.setOnClickListener(this);
        buttonConatiner = (LinearLayout)findViewById(R.id.purchaseButtonContainer);
        purchaseError = (TextView)findViewById(R.id.purchaseErrorText);
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            buyNow.setTextColor(getResources().getColor(R.color.cardview_dark_background));
            addToCart.setTextColor(getResources().getColor(R.color.cardview_dark_background));
        }
    }

    private void setValue(BookObject bookObject){
        name.setText(bookObject.getName());
        publisher.setText(bookObject.getPublisher());
        edition.setText(getResources().getString(R.string.bookEdition)+" : "+bookObject.getEdition());
        if(bookObject.getDescription().equalsIgnoreCase("")||bookObject.getDescription().isEmpty()){
            description.setVisibility(View.GONE);
        }else {
            description.setText(getResources().getString(R.string.purchaseDescription)+" : "+ bookObject.getDescription());
        }
        cateogory.setText(getResources().getString(R.string.bookCateogory)+" : "+ bookObject.getCateogory());
        condition.setText(getResources().getString(R.string.bookCondition)+" : "+ bookObject.getCondition());
        imageHolder.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color));
        buyNow.setText(" Buy Now "+ " र " +bookObject.getSellingPrice());
    }

    private void getUrl(){
        urls = new ArrayList<>();
        String baseUrl = getResources().getString(R.string.urlBucetHost)+getResources().getString(R.string.urlBucketName);
        if(!bObject.getPhoto0().equalsIgnoreCase("null")&&bObject.getPhoto0()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto0());
        }if(!bObject.getPhoto1().equalsIgnoreCase("null")&&bObject.getPhoto1()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto1());
        }if(!bObject.getPhoto2().equalsIgnoreCase("null")&&bObject.getPhoto2()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto2());
        }if(!bObject.getPhoto3().equalsIgnoreCase("null")&&bObject.getPhoto3()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto3());
        }if(!bObject.getPhoto4().equalsIgnoreCase("null")&&bObject.getPhoto4()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto4());
        }if(!bObject.getPhoto5().equalsIgnoreCase("null")&&bObject.getPhoto5()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto5());
        }if(!bObject.getPhoto6().equalsIgnoreCase("null")&&bObject.getPhoto6()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto6());
        } if(!bObject.getPhoto7().equalsIgnoreCase("null")&&bObject.getPhoto7()!=null){
            urls.add(baseUrl+"/"+bObject.getPhoto7());
        }
        imageAdapter = new adapterPurchaseImage(getApplicationContext(),urls,0);
        imageHolder.setAdapter(imageAdapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.purchaseAddTocart:
                if(getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload))!=0){
                    AlertDialog.Builder delete  = new AlertDialog.Builder(PurchaseActivity.this);
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
                }else {
                    checkInCart(bObject);
                }
            break;
            case R.id.purchaseBuy:
                if(getIntent().getExtras().getInt(getResources().getString(R.string.intentfromupload))!=0){
                    Intent detail  = new Intent(getApplicationContext(),AddBook.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intentEditObject), bObject);
                    detail.putExtras(b);
                    startActivityForResult(detail,mEditRequestCode);
                }else {
                  checkSold(bObject.getBid());
                }
                break;
        }
    }

    private void showPurchaseDialog(){
        dialogFragmentPurchase dialogFragmentPurchase = new dialogFragmentPurchase();
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.bundleBookName),name.getText().toString());
        args.putString(getResources().getString(R.string.bundleBookPublisher),publisher.getText().toString());
        args.putInt(getResources().getString(R.string.bundleBookPrice),bObject.getSellingPrice());
        args.putString(getResources().getString(R.string.bundleBookSellerUid),bObject.getUserId());
        args.putInt(getResources().getString(R.string.bundleBookBookId),bObject.getBid());
        dialogFragmentPurchase.setArguments(args);
        dialogFragmentPurchase.show(getSupportFragmentManager(),"purchase");
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildSoldUri(bid), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    showIfAvailable(response,dw);
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

    private void showIfAvailable(JSONArray status,Dialog d) throws JSONException {
        if (status.length() > 0) {
            JSONObject obj = status.getJSONObject(0);
            int statusCode = obj.getInt("status");
            d.dismiss();
            if (statusCode == 1) {
               Toast.makeText(getApplicationContext(),"Book sold",Toast.LENGTH_LONG).show();
            }else {
                showPurchaseDialog();
            }
        }
    }

    private String buildDeleteUri(){
        String host = getResources().getString(R.string.urlServer);
        String deleteBook = getResources().getString(R.string.urlDeleteSingle);
        String url = host + deleteBook;
        String bidQuery = "id";
        int bidValue = bObject.getBid();
        return Uri.parse(url).buildUpon().appendQueryParameter(bidQuery, String.valueOf(bidValue)).build().toString();
    }

    private void deleteItem() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildDeleteUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(PurchaseActivity.this, StartActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void removeBook() {
        if(bObject!=null){
            int count = getContentResolver().delete(Uri.withAppendedPath(CartTables.mCartContentUri,String.valueOf(bObject.getItemId())),null,null);
            if(count==0){
                Toast.makeText(getApplicationContext(),"Error while removing item",Toast.LENGTH_SHORT).show();
            }else {
                setCartText();
                Toast.makeText(getApplicationContext(),"Item Removed",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkInCart(BookObject bObject){
        Cursor c = getContentResolver().query(CartTables.mCartContentUri,null,null,null,null);
        if(c.getCount()==0){
            insertVal();
        }else {
            while (c.moveToNext()){
                if(c.getInt(c.getColumnIndex(tablecart.mUid))==bObject.getItemId()){
                    removeBook();
                    return;
                }
            }insertVal();
        }
    }

    private void setCartText(){
        Cursor c = getContentResolver().query(CartTables.mCartContentUri,null,null,null,null);
        if(c.getCount()==0){
            addToCart.setText("Add to cart");
        }
        while (c.moveToNext()){
            if(c.getInt(c.getColumnIndex(tablecart.mUid))==bObject.getItemId()){
                addToCart.setText("Remove from cart");
                return;
            }else {
                addToCart.setText("Add to cart");
            }
        }
    }

    private void insertVal() {
        ContentValues cv = new ContentValues();
        cv.put(tablecart.mBuid,bObject.getBid());
        cv.put(tablecart.mUid,bObject.getItemId());
        cv.put(tablecart.mName,bObject.getName());
        cv.put(tablecart.mPublisher,bObject.getPublisher());
        cv.put(tablecart.mCostPrice,bObject.getCostPrice());
        cv.put(tablecart.mSellingPrice,bObject.getSellingPrice());
        cv.put(tablecart.mEdition,bObject.getEdition());
        cv.put(tablecart.mCondition,bObject.getCondition());
        cv.put(tablecart.mCateogory,bObject.getCateogory());
        cv.put(tablecart.mDescription,bObject.getDescription());
        cv.put(tablecart.mUserId,bObject.getUserId());
        cv.put(tablecart.mPhoto0,bObject.getPhoto0());
        cv.put(tablecart.mPhoto1,bObject.getPhoto1());
        cv.put(tablecart.mPhoto2,bObject.getPhoto2());
        cv.put(tablecart.mPhoto3,bObject.getPhoto3());
        cv.put(tablecart.mPhoto4,bObject.getPhoto4());
        cv.put(tablecart.mPhoto5,bObject.getPhoto5());
        cv.put(tablecart.mPhoto6,bObject.getPhoto6());
        cv.put(tablecart.mPhoto7,bObject.getPhoto7());
        cv.put(tablecart.mstatus,bObject.getStatus());
        Uri u = getContentResolver().insert(CartTables.mCartContentUri,cv);
        Toast.makeText(getApplicationContext(),"Added to cart",Toast.LENGTH_SHORT).show();
        setCartText();
    }
}

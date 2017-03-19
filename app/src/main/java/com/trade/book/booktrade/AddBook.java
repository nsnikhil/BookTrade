package com.trade.book.booktrade;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.data.Tables;
import com.trade.book.booktrade.data.Tables.tableOne;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class AddBook extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbarAddBook;
    FloatingActionButton addBookDone;
    EditText name,publisher,costPrice,sellingPrice,edition,comment;
    Spinner condition,cateogory;
    ImageView emptyState,newImage;
    private static final String mNullValue = "N/A";
    //private static final int mUserId =  25478;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        initilize();
        setClickListener();
    }

    private void setClickListener() {
        addBookDone.setOnClickListener(this);
    }

    private void initilize() {
        toolbarAddBook = (Toolbar)findViewById(R.id.toolbarAddBook);
        setSupportActionBar(toolbarAddBook);
        name = (EditText)findViewById(R.id.addBookName);
        publisher = (EditText)findViewById(R.id.addBookPublisher);
        costPrice = (EditText)findViewById(R.id.addBookCostPrice);
        sellingPrice = (EditText)findViewById(R.id.addBookSellingPrice);
        edition = (EditText)findViewById(R.id.addBookEdition);
        comment = (EditText)findViewById(R.id.addBookComments);
        addBookDone = (FloatingActionButton)findViewById(R.id.addBookDone);
        condition = (Spinner) findViewById(R.id.addBookCondition);
        cateogory = (Spinner)findViewById(R.id.addBookCateogory);
        emptyState = (ImageView)findViewById(R.id.addBookImageEmptyState);
        newImage = (ImageView)findViewById(R.id.addBookNewImage);

        ArrayAdapter<CharSequence> spinnerConditionAdapter = ArrayAdapter.createFromResource(this, R.array.bookConditionEntry, android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condition.setAdapter(spinnerConditionAdapter);

        ArrayAdapter<CharSequence> spinnerCateogorynAdapter = ArrayAdapter.createFromResource(this, R.array.bookCateogories, android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateogory.setAdapter(spinnerCateogorynAdapter);

    }

    private String buildUrl(){
        String host = getResources().getString(R.string.urlServer);
        String insertFilename = getResources().getString(R.string.urlInsert);
        String url = host+insertFilename;
        String nameQuery = "nm";
        String nameValue = name.getText().toString();
        String publisherQuery = "pb";
        String publisherValue = publisher.getText().toString();
        String costPriceQuery = "cp";
        String costPriceValue = costPrice.getText().toString();
        String sellingPriceQuery = "sp";
        String sellingPriceValue = sellingPrice.getText().toString();
        String editionQuery = "ed";
        String editionValue = edition.getText().toString();
        String commentQuery = "ds";
        String commentValue = comment.getText().toString();
        String conditiontQuery = "cd";
        String conditionValue = condition.getSelectedItem().toString();;
        String cateogoryQuery = "ct";
        String cateogoryValue = cateogory.getSelectedItem().toString();;
        String userIdQuery = "uid";
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userIdKey = spf.getString(getResources().getString(R.string.prefAccountId),mNullValue);
        String userIdValue = String.valueOf(userIdKey);
        return  Uri.parse(url).buildUpon()
                .appendQueryParameter(nameQuery,nameValue)
                .appendQueryParameter(publisherQuery,publisherValue)
                .appendQueryParameter(costPriceQuery,costPriceValue)
                .appendQueryParameter(sellingPriceQuery,sellingPriceValue)
                .appendQueryParameter(editionQuery,editionValue)
                .appendQueryParameter(commentQuery,commentValue)
                .appendQueryParameter(conditiontQuery,conditionValue)
                .appendQueryParameter(cateogoryQuery,cateogoryValue)
                .appendQueryParameter(userIdQuery,userIdValue)
                .build()
                .toString();
    }


    private boolean verifyFields(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(name.getText().toString().isEmpty()||name.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter the name of the book",Toast.LENGTH_SHORT).show();
            return false;
        }else if(publisher.getText().toString().isEmpty()||publisher.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter the name of publisher",Toast.LENGTH_SHORT).show();
            return false;
        } else if(costPrice.getText().toString().isEmpty()||costPrice.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter the original cost price",Toast.LENGTH_SHORT).show();
            return false;
        }else if(sellingPrice.getText().toString().isEmpty()||sellingPrice.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter the selling price",Toast.LENGTH_SHORT).show();
            return false;
        }else if(edition.getText().toString().isEmpty()||edition.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter the edition",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Integer.parseInt(costPrice.getText().toString())<Integer.parseInt(sellingPrice.getText().toString())){
            Toast.makeText(this,"Selling Price cannot be greater than cost price",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Integer.parseInt(edition.getText().toString())>2018||Integer.parseInt(edition.getText().toString())<2000){
            Toast.makeText(this,"Enter a valid edition",Toast.LENGTH_SHORT).show();
            return false;
        }else if(spf.getString(getResources().getString(R.string.prefAccountId),mNullValue).equalsIgnoreCase(mNullValue)){
            Toast.makeText(this,"Invalid User Id",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addBookDone:
                if(verifyFields()){
                    RequestQueue request = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUrl(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, null);
                            finish();
                            startActivity(new Intent(AddBook.this,MainActivity.class));
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        }
                    });
                    request.add(stringRequest);
                }
            break;
        }
    }
}

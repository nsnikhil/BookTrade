package com.trade.book.booktrade;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.adapters.*;
import com.trade.book.booktrade.objects.BookObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class AddBook extends AppCompatActivity implements View.OnClickListener {

    private static final String mNullValue = "N/A";
    private static final int CAMERA_REQUEST_CODE = 154;
    private static final int GALLERY_REQUEST_CODE = 155;
    static int imageCount = 0;
    Toolbar toolbarAddBook;
    FloatingActionButton addBookDone;
    EditText name, publisher, costPrice, sellingPrice, edition, comment;
    Spinner condition, cateogory;
    ImageView emptyState, newImage;
    RecyclerView imageContainer;
    ArrayList<Bitmap> imageList;
    ArrayList<String> imageUrls;
    adapterImage imageAdapter;
    //adapterPurchaseImage editImageAdapter;
    String tempImageFolder = "tempImage";
    String[] fileArrayNames = {null, null, null, null, null, null, null, null};
    File[] fileArray;
    BookObject bookEditObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        initilize();
        setClickListener();
        if (getIntent().getExtras() != null) {
            bookEditObject = (BookObject) getIntent().getExtras().getSerializable(getResources().getString(R.string.intentEditObject));
            setValue(bookEditObject);

        }
    }

    private void setTwoVal() {
        name.setText(getIntent().getExtras().getString(getResources().getString(R.string.intentRequestBookName)));
        publisher.setText(getIntent().getExtras().getString(getResources().getString(R.string.intentRequestBookPublisher)));
    }

    private void setValue(BookObject bookEditObject) {
        name.setText(bookEditObject.getName());
        publisher.setText(bookEditObject.getPublisher());
        sellingPrice.setText(bookEditObject.getSellingPrice() + "");
        edition.setText(bookEditObject.getEdition() + "");
        costPrice.setText(bookEditObject.getCostPrice() + "");
        if (!bookEditObject.getDescription().isEmpty() || bookEditObject.getDescription().length() != 0) {
            comment.setText(bookEditObject.getDescription());
        }
        setCondition();
        setCategory();
        buildImageUrl();
    }

    private void setCondition() {
        String[] cdt = getResources().getStringArray(R.array.bookConditionEntry);
        for (int i = 0; i < cdt.length; i++) {
            if (cdt[i].equalsIgnoreCase(bookEditObject.getCondition())) {
                condition.setSelection(i);
                return;
            }
        }
    }

    private void setCategory() {
        String[] cat = getResources().getStringArray(R.array.bookCateogories);
        for (int i = 0; i < cat.length; i++) {
            if (cat[i].equalsIgnoreCase(bookEditObject.getCateogory())) {
                cateogory.setSelection(i);
                return;
            }
        }
    }

    private void buildImageUrl() {
        imageUrls = new ArrayList<>();
        String baseUrl = getResources().getString(R.string.urlBucetHost) + getResources().getString(R.string.urlBucketName);
        if (!bookEditObject.getPhoto0().equalsIgnoreCase("null") && bookEditObject.getPhoto0() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto0());
        }
        if (!bookEditObject.getPhoto1().equalsIgnoreCase("null") && bookEditObject.getPhoto1() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto1());
        }
        if (!bookEditObject.getPhoto2().equalsIgnoreCase("null") && bookEditObject.getPhoto2() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto2());
        }
        if (!bookEditObject.getPhoto3().equalsIgnoreCase("null") && bookEditObject.getPhoto3() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto3());
        }
        if (!bookEditObject.getPhoto4().equalsIgnoreCase("null") && bookEditObject.getPhoto4() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto4());
        }
        if (!bookEditObject.getPhoto5().equalsIgnoreCase("null") && bookEditObject.getPhoto5() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto5());
        }
        if (!bookEditObject.getPhoto6().equalsIgnoreCase("null") && bookEditObject.getPhoto6() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto6());
        }
        if (!bookEditObject.getPhoto7().equalsIgnoreCase("null") && bookEditObject.getPhoto7() != null) {
            imageUrls.add(baseUrl + "/" + bookEditObject.getPhoto7());
        }
        getImages();
        //editImageAdapter = new adapterPurchaseImage(getApplicationContext(), imageUrls, 1);
        //imageContainer.setAdapter(null);
        //imageContainer.setAdapter(editImageAdapter);
    }

    private void getImages() {
        for (int i = 0; i < imageUrls.size(); i++) {
            new downloadImages().execute(imageUrls.get(i));
        }
    }

    private void addBitmap(String s) {
        URL u = null;
        Bitmap img = null;
        HttpURLConnection htcp = null;
        InputStream is = null;
        try {
            u = new URL(s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            htcp = (HttpURLConnection) u.openConnection();
            htcp.setRequestMethod("GET");
            htcp.connect();
            if (htcp.getResponseCode() == 200) {
                is = htcp.getInputStream();
                img = BitmapFactory.decodeStream(is);
                imageList.add(img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (htcp != null) {
                htcp.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setClickListener() {
        addBookDone.setOnClickListener(this);
        newImage.setOnClickListener(this);
    }

    private void initilize() {
        toolbarAddBook = (Toolbar) findViewById(R.id.toolbarAddBook);
        setSupportActionBar(toolbarAddBook);
        name = (EditText) findViewById(R.id.addBookName);
        publisher = (EditText) findViewById(R.id.addBookPublisher);
        costPrice = (EditText) findViewById(R.id.addBookCostPrice);
        sellingPrice = (EditText) findViewById(R.id.addBookSellingPrice);
        edition = (EditText) findViewById(R.id.addBookEdition);
        comment = (EditText) findViewById(R.id.addBookComments);
        addBookDone = (FloatingActionButton) findViewById(R.id.addBookDone);
        condition = (Spinner) findViewById(R.id.addBookCondition);
        cateogory = (Spinner) findViewById(R.id.addBookCateogory);
        emptyState = (ImageView) findViewById(R.id.addBookImageEmptyState);
        newImage = (ImageView) findViewById(R.id.addBookNewImage);

        imageContainer = (RecyclerView) findViewById(R.id.addBookView);
        imageContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        imageList = new ArrayList<>();
        imageAdapter = new adapterImage(getApplicationContext(), imageList, imageCount);
        imageContainer.setAdapter(imageAdapter);
        fileArray = new File[fileArrayNames.length];

        ArrayAdapter<CharSequence> spinnerConditionAdapter = ArrayAdapter.createFromResource(this, R.array.bookConditionEntry, android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condition.setAdapter(spinnerConditionAdapter);

        ArrayAdapter<CharSequence> spinnerCateogorynAdapter = ArrayAdapter.createFromResource(this, R.array.bookCateogories, android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateogory.setAdapter(spinnerCateogorynAdapter);
    }

    private String buildUrl() {
        String host = getResources().getString(R.string.urlServer);
        String insertFilename = getResources().getString(R.string.urlInsert);
        String url = host + insertFilename;
        String nameQuery = "nm";
        String nameValue = name.getText().toString().trim();
        String publisherQuery = "pb";
        String publisherValue = publisher.getText().toString().trim();
        String costPriceQuery = "cp";
        String costPriceValue = costPrice.getText().toString().trim();
        String sellingPriceQuery = "sp";
        String sellingPriceValue = sellingPrice.getText().toString().trim();
        String editionQuery = "ed";
        String editionValue = edition.getText().toString().trim();
        String commentQuery = "ds";
        String commentValue = comment.getText().toString().trim();
        String conditiontQuery = "cd";
        String conditionValue = condition.getSelectedItem().toString().trim();
        ;
        String cateogoryQuery = "ct";
        String cateogoryValue = cateogory.getSelectedItem().toString().trim();
        ;
        String userIdQuery = "uid";

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userIdKey = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);
        String userIdValue = String.valueOf(userIdKey);

        String picyQuery0 = "p0";
        String picyValue0 = fileArrayNames[0];
        String picyQuery1 = "p1";
        String picyValue1 = fileArrayNames[1];
        String picyQuery2 = "p2";
        String picyValue2 = fileArrayNames[2];
        String picyQuery3 = "p3";
        String picyValue3 = fileArrayNames[3];
        String picyQuery4 = "p4";
        String picyValue4 = fileArrayNames[4];
        String picyQuery5 = "p5";
        String picyValue5 = fileArrayNames[5];
        String picyQuery6 = "p6";
        String picyValue6 = fileArrayNames[6];
        String picyQuery7 = "p7";
        String picyValue7 = fileArrayNames[7];

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(nameQuery, nameValue)
                .appendQueryParameter(publisherQuery, publisherValue)
                .appendQueryParameter(costPriceQuery, costPriceValue)
                .appendQueryParameter(sellingPriceQuery, sellingPriceValue)
                .appendQueryParameter(editionQuery, editionValue)
                .appendQueryParameter(commentQuery, commentValue)
                .appendQueryParameter(conditiontQuery, conditionValue)
                .appendQueryParameter(cateogoryQuery, cateogoryValue)
                .appendQueryParameter(userIdQuery, userIdValue)
                .appendQueryParameter(picyQuery0, picyValue0)
                .appendQueryParameter(picyQuery1, picyValue1)
                .appendQueryParameter(picyQuery2, picyValue2)
                .appendQueryParameter(picyQuery3, picyValue3)
                .appendQueryParameter(picyQuery4, picyValue4)
                .appendQueryParameter(picyQuery5, picyValue5)
                .appendQueryParameter(picyQuery6, picyValue6)
                .appendQueryParameter(picyQuery7, picyValue7)
                .build()
                .toString();
    }

    private String buildUpdateUrl() {
        String host = getResources().getString(R.string.urlServer);
        String updateFile = getResources().getString(R.string.urlUpdateSingle);
        String url = host + updateFile;

        String bookIdQuery = "bid";
        int bookIdValue = bookEditObject.getBid();

        String nameQuery = "nm";
        String nameValue = name.getText().toString().trim();
        String publisherQuery = "pb";
        String publisherValue = publisher.getText().toString().trim();
        String costPriceQuery = "cp";
        String costPriceValue = costPrice.getText().toString().trim();
        String sellingPriceQuery = "sp";
        String sellingPriceValue = sellingPrice.getText().toString().trim();
        String editionQuery = "ed";
        String editionValue = edition.getText().toString().trim();
        String commentQuery = "ds";
        String commentValue = comment.getText().toString().trim();
        String conditiontQuery = "cd";
        String conditionValue = condition.getSelectedItem().toString().trim();

        String cateogoryQuery = "ct";
        String cateogoryValue = cateogory.getSelectedItem().toString().trim();


        String picyQuery0 = "p0";
        String picyValue0 = fileArrayNames[0];
        String picyQuery1 = "p1";
        String picyValue1 = fileArrayNames[1];
        String picyQuery2 = "p2";
        String picyValue2 = fileArrayNames[2];
        String picyQuery3 = "p3";
        String picyValue3 = fileArrayNames[3];
        String picyQuery4 = "p4";
        String picyValue4 = fileArrayNames[4];
        String picyQuery5 = "p5";
        String picyValue5 = fileArrayNames[5];
        String picyQuery6 = "p6";
        String picyValue6 = fileArrayNames[6];
        String picyQuery7 = "p7";
        String picyValue7 = fileArrayNames[7];

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bookIdQuery, String.valueOf(bookIdValue))
                .appendQueryParameter(nameQuery, nameValue)
                .appendQueryParameter(publisherQuery, publisherValue)
                .appendQueryParameter(costPriceQuery, costPriceValue)
                .appendQueryParameter(sellingPriceQuery, sellingPriceValue)
                .appendQueryParameter(editionQuery, editionValue)
                .appendQueryParameter(commentQuery, commentValue)
                .appendQueryParameter(conditiontQuery, conditionValue)
                .appendQueryParameter(cateogoryQuery, cateogoryValue)
                .appendQueryParameter(picyQuery0, picyValue0)
                .appendQueryParameter(picyQuery1, picyValue1)
                .appendQueryParameter(picyQuery2, picyValue2)
                .appendQueryParameter(picyQuery3, picyValue3)
                .appendQueryParameter(picyQuery4, picyValue4)
                .appendQueryParameter(picyQuery5, picyValue5)
                .appendQueryParameter(picyQuery6, picyValue6)
                .appendQueryParameter(picyQuery7, picyValue7)
                .build()
                .toString();
    }

    private boolean verifyFields() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (name.getText().toString().isEmpty() || name.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter the name of the book", Toast.LENGTH_SHORT).show();
            return false;
        } else if (publisher.getText().toString().isEmpty() || publisher.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter the name of publisher", Toast.LENGTH_SHORT).show();
            return false;
        } else if (costPrice.getText().toString().isEmpty() || costPrice.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter the original cost price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (sellingPrice.getText().toString().isEmpty() || sellingPrice.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter the selling price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edition.getText().toString().isEmpty() || edition.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter the edition", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(costPrice.getText().toString()) < Integer.parseInt(sellingPrice.getText().toString())) {
            Toast.makeText(this, "Selling Price cannot be greater than cost price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(edition.getText().toString()) > 2018 || Integer.parseInt(edition.getText().toString()) < 2000) {
            Toast.makeText(this, "Enter a valid edition", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spf.getString(getResources().getString(R.string.prefAccountId), mNullValue).equalsIgnoreCase(mNullValue)) {
            Toast.makeText(this, "Invalid User Id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (imageList.size() < 1) {
            Toast.makeText(this, "You need to upload atleast one image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void makeNames() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String id = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);
        for (int i = 0; i < imageList.size(); i++) {
            fileArrayNames[i] = id + name.getText().toString() + i + ".jpg";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookDone:
                if (getIntent().getExtras() != null) {
                    if (verifyFields()) {
                        makeNames();
                        for (int i = 0; i < imageList.size(); i++) {
                            makeFile(fileArrayNames[i], i);
                        }
                        RequestQueue request = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUrl(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, null);
                                finish();
                                startActivity(new Intent(AddBook.this, MainActivity.class));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        request.add(stringRequest);
                    }
                } else {
                    if (verifyFields()) {
                        makeNames();
                        for (int i = 0; i < imageList.size(); i++) {
                            makeFile(fileArrayNames[i], i);
                        }
                        RequestQueue request = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUrl(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, null);
                                finish();
                                startActivity(new Intent(AddBook.this, MainActivity.class));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        request.add(stringRequest);
                    }
                }
                break;
            case R.id.addBookNewImage:
                if (imageList.size() < 8) {
                    chooseImageAction();
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot add more images", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String buildDeleteRequestUri() {
        String server = getResources().getString(R.string.urlServer);
        String requestAdd = getResources().getString(R.string.urlRequestDelete);
        String url = server + requestAdd;
        String ridQuery = "rid";
        int ridValue = getIntent().getExtras().getInt(getResources().getString(R.string.intentRequestBookRid));
        return Uri.parse(url).buildUpon().appendQueryParameter(ridQuery, String.valueOf(ridValue)).build().toString();
    }

    private void deleteRequest() {
        RequestQueue requestObject = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildDeleteRequestUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestObject.add(stringRequest);
    }

    private void chooseImageAction() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(AddBook.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddBook.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take a picture");
        arrayAdapter.add("Choose from galley");
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent intentcam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentcam, CAMERA_REQUEST_CODE);
                }
                if (position == 1) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                }
            }
        });
        choosePath.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(data.getData());
                    Bitmap b = BitmapFactory.decodeStream(is);
                    imageList.add(b);
                    imageContainer.swapAdapter(imageAdapter, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageList.add(imageBitmap);
                imageContainer.swapAdapter(imageAdapter, true);
            }
        }
    }

    private void makeFile(String fileName, int position) {
        FileOutputStream fos = null;
        File fldr = getExternalFilesDir(tempImageFolder);
        if (!fldr.exists()) {
            fldr.mkdir();
        }
        fileArray[position] = new File(fldr, fileName);
        try {
            fos = new FileOutputStream(fileArray[position]);
            imageList.get(position).compress(Bitmap.CompressFormat.PNG, 100, fos);
            new uploadAsync(fileArray[position], fileName).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(File f, String filename) throws IOException {
        AmazonS3 s3 = new AmazonS3Client(getCredentials());
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        String bucket = getResources().getString(R.string.urlBucketName);
        TransferObserver observer = transferUtility.upload(bucket, filename, f);
    }

    private CognitoCachingCredentialsProvider getCredentials() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-1:b3d1bc05-d6f7-4fc7-bcdb-dbb9b7fd1d4c", // Identity Pool ID
                Regions.AP_NORTHEAST_1 // Region
        );
        return credentialsProvider;
    }

    public class downloadImages extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            addBitmap(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageContainer.swapAdapter(imageAdapter, true);
        }
    }

    public class uploadAsync extends AsyncTask<Void, Void, Void> {

        File f;
        String fn;

        uploadAsync(File file, String fileName) {
            f = file;
            fn = fileName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                uploadImage(f, fn);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }
}

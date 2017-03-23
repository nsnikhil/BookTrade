package com.trade.book.booktrade;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.adapters.*;

import java.util.ArrayList;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener{

    TextView name,publisher,edition,description,cateogory,condition;
    Button buyNow,addToCart;
    RecyclerView imageHolder;
    BookObject bObject = null;
    adapterPurchaeeImage imageAdapter;
    ArrayList<String> urls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        initilize();
        if(getIntent().getExtras()!=null){
            bObject = (BookObject) getIntent().getExtras().getSerializable(getResources().getString(R.string.intenKeyObejct));
            setValue(bObject);
        }
        setCartText();
        getUrl();
    }

    private void initilize() {
        name = (TextView)findViewById(R.id.purchaseName);
        publisher = (TextView)findViewById(R.id.purchasePublisher);
        edition = (TextView)findViewById(R.id.purchaseEdition);
        description = (TextView)findViewById(R.id.purchaseDescription);
        cateogory = (TextView)findViewById(R.id.purchaseCateogory);
        condition = (TextView)findViewById(R.id.purchaseCondition);
        buyNow = (Button)findViewById(R.id.purchaseBuy);

        imageHolder = (RecyclerView)findViewById(R.id.purchaseBookView);
        imageHolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        addToCart = (Button)findViewById(R.id.purchaseAddTocart);
        addToCart.setOnClickListener(this);

    }

    private void setValue(BookObject bookObject){
        name.setText(bookObject.getName());
        publisher.setText(bookObject.getPublisher());
        edition.setText("Edition : "+ bookObject.getEdition());
        description.setText("Description : "+ bookObject.getDescription());
        cateogory.setText("Cateogory : "+ bookObject.getCateogory());
        condition.setText("Condition : "+ bookObject.getCondition());
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
        imageAdapter = new adapterPurchaeeImage(getApplicationContext(),urls);
        imageHolder.setAdapter(imageAdapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.purchaseAddTocart:
               checkInCart(bObject);
            break;
        }
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
        Uri u = getContentResolver().insert(CartTables.mCartContentUri,cv);
        Toast.makeText(getApplicationContext(),"Added to cart",Toast.LENGTH_SHORT).show();
        setCartText();
    }
}

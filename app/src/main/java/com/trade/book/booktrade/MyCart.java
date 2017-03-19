package com.trade.book.booktrade;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.cartData.CartTables;


public class MyCart extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView bookCartGrid;
    ImageView noItem;
    Toolbar cartToolbar;
    FloatingActionButton checkOut;
    private static final int mCartLoaderId = 1;
    CartAdapter cartAdapter;

    public MyCart() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kart);
        initilize();
        loadCart();
        setEmpty();
    }


    private void initilize() {
        bookCartGrid = (GridView)findViewById(R.id.cartList);
        noItem = (ImageView)findViewById(R.id.cartEmpty);
        cartAdapter = new CartAdapter(getApplicationContext(),null);
        bookCartGrid.setAdapter(cartAdapter);
        cartToolbar = (Toolbar)findViewById(R.id.toolbarCart);
        setSupportActionBar(cartToolbar);
        checkOut = (FloatingActionButton)findViewById(R.id.cartCheckOut);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"CheckOut",Toast.LENGTH_SHORT).show();
            }
        });
        bookCartGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if(c.moveToFirst()){
                    BookObject bookObject = new BookObject (c.getString(c.getColumnIndex(tablecart.mName)),
                            c.getString(c.getColumnIndex(tablecart.mPublisher)),
                            c.getInt(c.getColumnIndex(tablecart.mCostPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mSellingPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mEdition)),
                            c.getString(c.getColumnIndex(tablecart.mDescription)),
                            c.getString(c.getColumnIndex(tablecart.mCondition)),
                            c.getString(c.getColumnIndex(tablecart.mCateogory)),
                            c.getInt(c.getColumnIndex(tablecart.mUserId)),
                            c.getInt(c.getColumnIndex(tablecart._ID)));
                    Intent detail  = new Intent(getApplicationContext(),PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    detail.putExtra(getResources().getString(R.string.intenfromcart),true);
                    startActivity(detail);
                }
            }
        });
    }

    private void setEmpty(){
        if(getContentResolver().query(CartTables.mCartContentUri,null,null,null,null).getCount()<=0){
            noItem.setVisibility(View.VISIBLE);
        }else {
            noItem.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mCartLoaderId:
                return new CursorLoader(getApplicationContext(),CartTables.mCartContentUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cartAdapter.swapCursor(data);
        setEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cartAdapter.swapCursor(null);
    }

    private void loadCart() {
        if (getSupportLoaderManager().getLoader(mCartLoaderId) == null) {
            getSupportLoaderManager().initLoader(mCartLoaderId, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(mCartLoaderId, null, this).forceLoad();
        }
    }
}

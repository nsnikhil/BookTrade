package com.trade.book.booktrade;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.trade.book.booktrade.fragments.*;


public class StartActivity extends AppCompatActivity {

    BottomNavigationView mBottomNaviagtionView;
    Toolbar mBottomToolbar;
    FloatingActionButton mFabAddBook;
    MaterialSearchView mBottomSearchView;
    private static final String[] colorArray = {"#5D4037","#FFA000","#D32F2F","#388E3C"};
    private static final String[] colorArrayDark = {"#3E2723","#FF6F00","#B71C1C","#1B5E20"};
    BookListFragment mFragmentBookList;
    RequestListFragment mRequestListFragment;
    AccountFragment mAccountFragment;
    MoreFragment mMoreFragment;
    MyCartFragment myCartFragment;
    RelativeLayout mBottomConatainer;
    int mAddBookRequestCode = 1080;
    ImageView errorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.tranparentNavBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialize(savedInstanceState);
        setClickListeners();
    }

    private void addFragments(Bundle savedInstanceState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mFragmentBookList = new BookListFragment();
            mRequestListFragment = new RequestListFragment();
            mAccountFragment =  new AccountFragment();;
            mMoreFragment = new MoreFragment();;
            myCartFragment = new MyCartFragment();;
            ft.add(R.id.bottomMainContainer, mFragmentBookList);
            ft.add(R.id.bottomMainContainer, mRequestListFragment);
            ft.add(R.id.bottomMainContainer, mAccountFragment);
            ft.add(R.id.bottomMainContainer, mMoreFragment);
            ft.add(R.id.bottomMainContainer, myCartFragment);
            ft.show(mFragmentBookList);
            ft.hide(mRequestListFragment);
            ft.hide(mAccountFragment);
            ft.hide(mMoreFragment);
            ft.hide(myCartFragment);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_menu, menu);
        MenuItem item = menu.findItem(R.id.menuBottomMainSearch);
        mBottomSearchView.setMenuItem(item);
        return true;
    }

    private void initialize(Bundle savedInstanceState) {
        mBottomNaviagtionView = (BottomNavigationView) findViewById(R.id.mainBottomNaviagtion);
        mBottomToolbar = (Toolbar)findViewById(R.id.bottomToolbar);
        setSupportActionBar(mBottomToolbar);
        mBottomSearchView = (MaterialSearchView)findViewById(R.id.bottomSearchView);
        mFabAddBook = (FloatingActionButton)findViewById(R.id.bottomFabAdd);
        mBottomConatainer = (RelativeLayout)findViewById(R.id.bottomMainContainer);
        errorImage = (ImageView)findViewById(R.id.bottomErrorImage);
        addOnConnection(savedInstanceState);
    }

    private void checkFirst() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean id = spf.getBoolean(getResources().getString(R.string.prefAccountIndicator), false);
        if (!id) {
            finish();
            Intent intro = new Intent(StartActivity.this, IntroActivity.class);
            intro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intro);
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void addOnConnection(Bundle savedInstanceState) {
        if (checkConnection()) {
            checkFirst();
            addFragments(savedInstanceState);
            errorImage.setVisibility(View.GONE);
            mBottomNaviagtionView.setVisibility(View.VISIBLE);
            mFabAddBook.setVisibility(View.VISIBLE);
        } else {
            removeOffConnection(savedInstanceState);
        }
    }

    private void removeOffConnection(final Bundle savedInstanceState) {
        errorImage.setVisibility(View.VISIBLE);
        mBottomNaviagtionView.setVisibility(View.GONE);
        mFabAddBook.setVisibility(View.GONE);
        Snackbar.make(mBottomConatainer, "No Internet", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.white))
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addOnConnection(savedInstanceState);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mAddBookRequestCode && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void setClickListeners(){
        mFabAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if(spf.getBoolean(getResources().getString(R.string.prefAccountIndicator),false)){
                    startActivityForResult(new Intent(StartActivity.this, AddBook.class), mAddBookRequestCode);
                }else {
                    checkFirst();
                }
            }
        });
        mBottomNaviagtionView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.bottomMenuBooks:
                        ft.show(mFragmentBookList);
                        ft.hide(mRequestListFragment);
                        ft.hide(mAccountFragment);
                        ft.hide(mMoreFragment);
                        ft.hide(myCartFragment);
                        bottomSelection(0);
                        break;
                    case R.id.bottomMenuCart:
                        ft.hide(mFragmentBookList);
                        ft.hide(mRequestListFragment);
                        ft.hide(mAccountFragment);
                        ft.hide(mMoreFragment);
                        ft.show(myCartFragment);
                        bottomSelection(2);
                        break;
                    case R.id.bottomMenuRequest:
                        ft.hide(mFragmentBookList);
                        ft.show(mRequestListFragment);
                        ft.hide(mAccountFragment);
                        ft.hide(mMoreFragment);
                        ft.hide(myCartFragment);
                        bottomSelection(1);
                        break;
                    case R.id.bottomMenuAccount:
                        ft.hide(mFragmentBookList);
                        ft.hide(mRequestListFragment);
                        ft.show(mAccountFragment);
                        ft.hide(mMoreFragment);
                        ft.hide(myCartFragment);
                        bottomSelection(3);
                        break;
                    case R.id.bottomMenuMore:
                        ft.hide(mFragmentBookList);
                        ft.hide(mRequestListFragment);
                        ft.hide(mAccountFragment);
                        ft.show(mMoreFragment);
                        ft.hide(myCartFragment);
                        bottomSelection(4);
                        break;
                }
                ft.commit();
                return false;
            }
        });
        mBottomSearchView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent search = new Intent(StartActivity.this,SearchActivity.class);
                search.putExtra(getResources().getString(R.string.intenSearchKey),s);
                search.setAction(Intent.ACTION_SEARCH);
                startActivity(search);
                mBottomSearchView.clearFocus();
                return false;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });
    }

    private void bottomSelection(int key) {
        invalidateOptionsMenu();
        MenuItem menuBook = mBottomNaviagtionView.getMenu().getItem(0).setChecked(false);
        MenuItem menuCart = mBottomNaviagtionView.getMenu().getItem(1).setChecked(false);
        MenuItem menuRequest = mBottomNaviagtionView.getMenu().getItem(2).setChecked(false);
        MenuItem menuAccount = mBottomNaviagtionView.getMenu().getItem(3).setChecked(false);
        MenuItem menuMore = mBottomNaviagtionView.getMenu().getItem(4).setChecked(false);

        switch (key) {
            case 0:
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor( getResources().getColor(R.color.colorPrimaryDark));
                    getWindow().setStatusBarColor( getResources().getColor(R.color.colorPrimaryDark));
                    mBottomToolbar.setBackground(getResources().getDrawable(R.drawable.toolbargradeint));
                    mBottomNaviagtionView.setItemBackgroundResource(R.drawable.booknav);
                }
                if(mFabAddBook.getVisibility()==View.GONE){
                    mFabAddBook.setVisibility(View.VISIBLE);
                }
                menuBook.setChecked(true);
                break;
            case 1:
                setColor(colorArray[1],colorArrayDark[1],0);
                menuCart.setChecked(true);
                break;
            case 2:
                setColor(colorArray[0],colorArrayDark[0],1);
                menuRequest.setChecked(true);
                break;
            case 3:
                setColor(colorArray[2],colorArrayDark[2],2);
                menuAccount.setChecked(true);
                break;
            case 4:
                setColor(colorArray[3],colorArrayDark[3],3);
                menuMore.setChecked(true);
                break;
        }
    }

    private void setColor(String color,String darkColor,int code){
        if(mFabAddBook.getVisibility()==View.VISIBLE){
            mFabAddBook.setVisibility(View.GONE);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor( Color.parseColor(darkColor));
            getWindow().setStatusBarColor( Color.parseColor(darkColor));
            mBottomToolbar.setBackgroundColor(Color.parseColor(color));
            switch (code){
                case 0:
                    mBottomNaviagtionView.setItemBackgroundResource(R.drawable.reqnav);
                    getSupportActionBar().setTitle(getResources().getString(R.string.navRequest));
                    break;
                case 1:
                    mBottomNaviagtionView.setItemBackgroundResource(R.drawable.cartnav);
                    getSupportActionBar().setTitle(getResources().getString(R.string.navMyCart));
                    break;
                case 2:
                    mBottomNaviagtionView.setItemBackgroundResource(R.drawable.accnav);
                    getSupportActionBar().setTitle(getResources().getString(R.string.navaccount));
                    break;
                case 3:
                    mBottomNaviagtionView.setItemBackgroundResource(R.drawable.morenav);
                    getSupportActionBar().setTitle(getResources().getString(R.string.navMore));
                    break;
            }
        }
    }

}

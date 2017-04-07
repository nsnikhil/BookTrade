package com.trade.book.booktrade;


import android.*;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.leakcanary.LeakCanary;
import com.trade.book.booktrade.fragments.*;
import com.trade.book.booktrade.interfaces.RequestListScrollChange;

import java.lang.reflect.Field;


public class StartActivity extends AppCompatActivity implements RequestListScrollChange {

    BottomNavigationView mBottomNaviagtionView;
    Toolbar mBottomToolbar;
    FloatingActionButton mFabAddBook;
    MaterialSearchView mBottomSearchView;
    private static final String[] colorArray = {"#5D4037", "#FFA000", "#455A64", "#388E3C"};
    private static final String[] colorArrayDark = {"#3E2723", "#FF6F00", "#263238", "#1B5E20"};
    BookPagerFragment mFragmentBookPager;
    RequestListFragment mRequestListFragment;
    AccountFragment mAccountFragment;
    MoreFragment mMoreFragment;
    MyCartFragment myCartFragment;
    RelativeLayout mBottomConatainer,mEntireContainer;
    int mAddBookRequestCode = 1080;
    ImageView errorImage;
    private static final int MY_WRITE_EXTERNAL_STORAGE_CODE = 556;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.tranparentNavBar);
        }
        super.onCreate(savedInstanceState);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_start);
        initialize(savedInstanceState);
        setClickListeners();
    }

    private void addFragments(Bundle savedInstanceState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mFragmentBookPager = new BookPagerFragment();
            mRequestListFragment = new RequestListFragment();
            mAccountFragment = new AccountFragment();
            mMoreFragment = new MoreFragment();
            myCartFragment = new MyCartFragment();
            ft.add(R.id.bottomMainContainer, mFragmentBookPager);
            ft.add(R.id.bottomMainContainer, mRequestListFragment);
            ft.add(R.id.bottomMainContainer, mAccountFragment);
            ft.add(R.id.bottomMainContainer, mMoreFragment);
            ft.add(R.id.bottomMainContainer, myCartFragment);
            ft.show(mFragmentBookPager);
            ft.hide(mRequestListFragment);
            ft.hide(mAccountFragment);
            ft.hide(mMoreFragment);
            ft.hide(myCartFragment);
            ft.commit();
        }
    }

    private void buildTapTarget(){
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.bottomFabAdd), "Click + to upload a book", "")
                .icon(getResources().getDrawable(R.drawable.ic_add_white_48dp))
                .targetCircleColor(R.color.colorAccent),
        new TapTargetView.Listener() {
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                view.dismiss(false);
                fabClick();
            }

            @Override
            public void onTargetCancel(TapTargetView view) {
                super.onTargetCancel(view);
                view.dismiss(false);
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                view.dismiss(false);
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                spf.edit().putInt(getResources().getString(R.string.prefFirstOpen),1).apply();
                view.dismiss(false);
                super.onTargetDismissed(view, userInitiated);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_menu, menu);
        MenuItem item = menu.findItem(R.id.menuBottomMainSearch);
        mBottomSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return false;
    }

    private void initialize(Bundle savedInstanceState) {
        mBottomNaviagtionView = (BottomNavigationView) findViewById(R.id.mainBottomNaviagtion);
        disableShiftMode(mBottomNaviagtionView);
        mBottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar);
        setSupportActionBar(mBottomToolbar);
        getSupportActionBar().setElevation(0);
        mBottomSearchView = (MaterialSearchView) findViewById(R.id.bottomSearchView);
        mFabAddBook = (FloatingActionButton) findViewById(R.id.bottomFabAdd);
        mBottomConatainer = (RelativeLayout) findViewById(R.id.bottomMainContainer);
        mEntireContainer = (RelativeLayout) findViewById(R.id.entireContainer);
        errorImage = (ImageView) findViewById(R.id.bottomErrorImage);
        addOnConnection(savedInstanceState);
    }

    private void checkFirst() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean id = spf.getBoolean(getResources().getString(R.string.prefAccountIndicator), false);
        if (!id) {
            finish();
            Intent intro = new Intent(StartActivity.this, IntroActivity.class);
            intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intro);
        }
    }

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
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
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(spf.getInt(getResources().getString(R.string.prefFirstOpen),0)==0){
                buildTapTarget();
            }
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

    private void fabClick(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (spf.getBoolean(getResources().getString(R.string.prefAccountIndicator), false)) {
            startActivityForResult(new Intent(StartActivity.this, AddBook.class), mAddBookRequestCode);
        } else {
            checkFirst();
        }
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE_CODE);
        }else {
            fabClick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fabClick();
            }
        }
    }

    private void setClickListeners() {
        mFabAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission();
            }
        });
        mBottomNaviagtionView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.bottomMenuBooks:
                        if (mFragmentBookPager.isHidden()) {
                            ft.show(mFragmentBookPager);
                            ft.hide(mRequestListFragment);
                            ft.hide(mAccountFragment);
                            ft.hide(mMoreFragment);
                            ft.hide(myCartFragment);
                            bottomSelection(0);
                        }
                        break;
                    case R.id.bottomMenuCart:
                        if (myCartFragment.isHidden()) {
                            ft.hide(mFragmentBookPager);
                            ft.hide(mRequestListFragment);
                            ft.hide(mAccountFragment);
                            ft.hide(mMoreFragment);
                            ft.show(myCartFragment);
                            bottomSelection(2);
                        }
                        break;
                    case R.id.bottomMenuRequest:
                        if (mRequestListFragment.isHidden()) {
                            ft.hide(mFragmentBookPager);
                            ft.show(mRequestListFragment);
                            ft.hide(mAccountFragment);
                            ft.hide(mMoreFragment);
                            ft.hide(myCartFragment);
                            bottomSelection(1);
                        }
                        break;
                    case R.id.bottomMenuAccount:
                        if (mAccountFragment.isHidden()) {
                            ft.hide(mFragmentBookPager);
                            ft.hide(mRequestListFragment);
                            ft.show(mAccountFragment);
                            ft.hide(mMoreFragment);
                            ft.hide(myCartFragment);
                            bottomSelection(3);
                        }
                        break;
                    case R.id.bottomMenuMore:
                        if (mMoreFragment.isHidden()) {
                            ft.hide(mFragmentBookPager);
                            ft.hide(mRequestListFragment);
                            ft.hide(mAccountFragment);
                            ft.show(mMoreFragment);
                            ft.hide(myCartFragment);
                            bottomSelection(4);
                        }
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
                Intent search = new Intent(StartActivity.this, SearchActivity.class);
                search.putExtra(getResources().getString(R.string.intenSearchKey), s);
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
                    //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    //getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    //mBottomToolbar.setBackground(getResources().getDrawable(R.drawable.toolbargradeint));
                    //mBottomNaviagtionView.setItemBackgroundResource(R.drawable.booknav);
                    getSupportActionBar().setElevation(0);
                }
                if (mFabAddBook.getVisibility() == View.GONE) {
                    mFabAddBook.setVisibility(View.VISIBLE);
                }
                menuBook.setChecked(true);
                break;
            case 1:
                setColor(colorArray[1], colorArrayDark[1], 0);
                menuCart.setChecked(true);
                break;
            case 2:
                setColor(colorArray[0], colorArrayDark[0], 1);
                menuRequest.setChecked(true);
                break;
            case 3:
                setColor(colorArray[2], colorArrayDark[2], 2);
                menuAccount.setChecked(true);
                break;
            case 4:
                setColor(colorArray[3], colorArrayDark[3], 3);
                menuMore.setChecked(true);
                break;
        }
    }


    private void setColor(String color, String darkColor, int code) {
        if (mFabAddBook.getVisibility() == View.VISIBLE) {
            mFabAddBook.setVisibility(View.GONE);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(getResources().getDimension(R.dimen.toolbarElevation));
            //getWindow().setNavigationBarColor(Color.parseColor(darkColor));
            // getWindow().setStatusBarColor(Color.parseColor(darkColor));
            //mBottomToolbar.setBackgroundColor(Color.parseColor(color));
        }
        switch (code) {
            case 0:
                //mBottomNaviagtionView.setItemBackgroundResource(R.drawable.reqnav);
                getSupportActionBar().setTitle(getResources().getString(R.string.navRequest));
                break;
            case 1:
                // mBottomNaviagtionView.setItemBackgroundResource(R.drawable.cartnav);
                getSupportActionBar().setTitle(getResources().getString(R.string.navMyCart));
                break;
            case 2:
                //  mBottomNaviagtionView.setItemBackgroundResource(R.drawable.accnav);
                getSupportActionBar().setTitle(getResources().getString(R.string.navaccount));
                break;
            case 3:
                //  mBottomNaviagtionView.setItemBackgroundResource(R.drawable.morenav);
                getSupportActionBar().setTitle(getResources().getString(R.string.navMore));
                break;
        }
    }

    @Override
    public void hideItems() {
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                animateOut();
            }
        }, 50);*/
    }

    private void animateOut() {
        if (!mFragmentBookPager.isHidden()) {
            if (mFabAddBook.getVisibility() == View.VISIBLE) {
                mFabAddBook.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFabAddBook.setVisibility(View.GONE);
                    }
                });
            }
        }
        if (mBottomNaviagtionView.getVisibility() == View.VISIBLE) {
            mBottomNaviagtionView.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBottomNaviagtionView.setVisibility(View.GONE);
                }
            });
        }
        if (mBottomToolbar.getVisibility() == View.VISIBLE) {
            mBottomToolbar.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBottomToolbar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void animateIn() {
        if (!mFragmentBookPager.isHidden()) {
            if (mFabAddBook.getVisibility() == View.GONE) {
                mFabAddBook.animate().alpha(1.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFabAddBook.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        if (mBottomNaviagtionView.getVisibility() == View.GONE) {
            mBottomNaviagtionView.animate().alpha(1.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBottomNaviagtionView.setVisibility(View.VISIBLE);
                }
            });
        }
        if (mBottomToolbar.getVisibility() == View.GONE) {
            mBottomToolbar.animate().alpha(1.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBottomToolbar.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public void showItems() {
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                animateIn();
            }
        }, 50);*/
    }
}

package com.trade.book.booktrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.trade.book.booktrade.data.Tables;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbarMain;
    FloatingActionButton mainAdd;
    DrawerLayout mainDrawerLayout;
    RelativeLayout mainLayout;
    ImageView errorImage;
    NavigationView mainNaviagtionView;
    SearchView searchView;
    ActionBarDrawerToggle mainDrawerToggle;
    ViewPager mainViewPager;
    TabLayout TabsMain;
    int mRequestCode = 1080;
    private static final String mNullValue = "N/A";
    CircularImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilize();
        initilizeDrawer();
        setClickListener();
    }

    private void checkFirst() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean id = spf.getBoolean(getResources().getString(R.string.prefAccountIndicator), false);
        if (!id) {
            finish();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void initilize() {
        toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        toolbarMain.setTitleTextColor(getResources().getColor(android.R.color.white));
        mainAdd = (FloatingActionButton) findViewById(R.id.mainAdd);
        mainViewPager = (ViewPager) findViewById(R.id.mainContainer);
        TabsMain = (TabLayout) findViewById(R.id.tabLayoutMain);
        errorImage = (ImageView) findViewById(R.id.errorImage);
        mainLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setHint(getResources().getString(R.string.searchHint));
        searchView.setOnMenuClickListener(new SearchView.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                mainDrawerLayout.openDrawer(Gravity.START);
            }
        });
        searchView.setOnVoiceClickListener(new SearchView.OnVoiceClickListener() {
            @Override
            public void onVoiceClick() {
                Toast.makeText(getApplicationContext(),"Will Start Voice Search",Toast.LENGTH_LONG).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent search = new Intent(MainActivity.this,SearchActivity.class);
                search.putExtra(getResources().getString(R.string.intenSearchKey),query);
                search.setAction(Intent.ACTION_SEARCH);
                startActivity(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        addOnConnection();
    }

    private void addOnConnection() {
        if (checkConnection()) {
            checkFirst();
            PagerAdapter lPagerAdapter = new PagerAdapter(getSupportFragmentManager());
            mainViewPager.setAdapter(lPagerAdapter);
            TabsMain.setupWithViewPager(mainViewPager);
            errorImage.setVisibility(View.GONE);
            TabsMain.setVisibility(View.VISIBLE);
            mainAdd.setVisibility(View.VISIBLE);
        } else {
            removeOffConnection();
        }
    }

    private void removeOffConnection() {
        TabsMain.setVisibility(View.GONE);
        mainAdd.setVisibility(View.GONE);
        errorImage.setVisibility(View.VISIBLE);
        Snackbar.make(mainLayout, "No Internet", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnConnection();
            }
        }).show();
    }

    private void initilizeDrawer() {
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        mainNaviagtionView = (NavigationView) findViewById(R.id.mainNaviagtionView);
        View v = mainNaviagtionView.getHeaderView(0);
        profileImage = (CircularImageView) v.findViewById(R.id.headerCircleView);
        if(getBitmap()!=null){
            profileImage.setImageBitmap(getBitmap());
        }else {
            profileImage.setImageResource(R.drawable.profile);
        }
        mainNaviagtionView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mainDrawerLayout.closeDrawers();
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                switch (item.getItemId()) {
                    case R.id.navItemMyCart:
                        if(spf.getBoolean(getResources().getString(R.string.prefAccountIndicator),false)){
                            startActivity(new Intent(MainActivity.this, MyCart.class));
                        }else {
                            checkFirst();
                        }
                        break;
                    case R.id.navItemAccount:
                        if(spf.getBoolean(getResources().getString(R.string.prefAccountIndicator),false)){
                            startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        }else {
                            checkFirst();
                        }
                        break;
                    case R.id.navItemSettings:
                        startActivity(new Intent(MainActivity.this, PrefActivity.class));
                        break;
                    case R.id.navItemFeedback:
                        Toast.makeText(getApplicationContext(), "Will Send Feedback once we have a public email id", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });
    }

    private Bitmap getBitmap() {
        Bitmap img = null;
        File folder = getExternalCacheDir();
        File fi = new File(folder, "profile.jpg");
        String fpath = String.valueOf(fi);
        img = BitmapFactory.decodeFile(fpath);
        return img;
    }

    private void setClickListener() {
        mainAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainAdd:
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if(spf.getBoolean(getResources().getString(R.string.prefAccountIndicator),false)){
                    startActivityForResult(new Intent(MainActivity.this, AddBook.class), mRequestCode);
                }else {
                    checkFirst();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode && resultCode == RESULT_OK) {
            this.finish();
        }
    }
}

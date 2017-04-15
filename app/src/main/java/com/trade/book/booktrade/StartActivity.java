package com.trade.book.booktrade;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.fragments.AccountFragment;
import com.trade.book.booktrade.fragments.BookPagerFragment;
import com.trade.book.booktrade.fragments.MoreFragment;
import com.trade.book.booktrade.fragments.MyCartFragment;
import com.trade.book.booktrade.fragments.RequestListFragment;
import com.trade.book.booktrade.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class StartActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String[] colorArray = {"#5D4037", "#FFA000", "#455A64", "#388E3C"};
    private static final String[] colorArrayDark = {"#3E2723", "#FF6F00", "#263238", "#1B5E20"};
    private static final String mNullValue = "N/A";
    private static final int MY_WRITE_EXTERNAL_STORAGE_CODE = 556;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 56;
    @BindView(R.id.bottomToolbar)
    Toolbar mBottomToolbar;
    @BindView(R.id.bottomFabAdd)
    FloatingActionButton mFabAddBook;
    @BindView(R.id.bottomSearchView)
    MaterialSearchView mBottomSearchView;
    BookPagerFragment mFragmentBookPager;
    RequestListFragment mRequestListFragment;
    AccountFragment mAccountFragment;
    MoreFragment mMoreFragment;
    MyCartFragment myCartFragment;
    @BindView(R.id.mainBottomNaviagtion)
    BottomNavigationView mBottomNaviagtionView;
    @BindView(R.id.bottomMainContainer)
    RelativeLayout mBottomConatainer;
    @BindView(R.id.entireContainer)
    RelativeLayout mEntireContainer;
    int mAddBookRequestCode = 1080;
    @BindView(R.id.bottomErrorImage)
    ImageView errorImage;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

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
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.tranparentNavBar);
        }
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        initialize(savedInstanceState);
        setClickListeners();
    }


    /*
    Adds Fragment to bottomnavgationview
     */
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

    /*
    build tap target for add book
     */
    private void buildTapTarget() {
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
                        spf.edit().putInt(getResources().getString(R.string.prefFirstOpen), 1).apply();
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


    /*
   initialize function
     */
    private void initialize(Bundle savedInstanceState) {
        disableShiftMode(mBottomNaviagtionView);
        setSupportActionBar(mBottomToolbar);
        getSupportActionBar().setElevation(0);
        addOnConnection(savedInstanceState);
    }

    /*
   check for account
     */
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

    /*
   check for connection
     */
    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /*
   add this on connection
     */
    private void addOnConnection(Bundle savedInstanceState) {
        if (checkConnection()) {
            checkFirst();
            verifyLocation();
            addFragments(savedInstanceState);
            errorImage.setVisibility(View.GONE);
            mBottomNaviagtionView.setVisibility(View.VISIBLE);
            mFabAddBook.setVisibility(View.VISIBLE);
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (spf.getInt(getResources().getString(R.string.prefFirstOpen), 0) == 0) {
                buildTapTarget();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                if (!spf.getString(getResources().getString(R.string.prefAccountId), mNullValue).equalsIgnoreCase(mNullValue)) {
                    if (refreshedToken != null) {
                        updateValues(refreshedToken);
                    }
                }
            } else {
                if (!spf.getString(getResources().getString(R.string.prefAccountId), mNullValue).equalsIgnoreCase(mNullValue)) {
                    preFetchValues(spf.getString(getResources().getString(R.string.prefAccountId), mNullValue));
                }
            }
        } else {
            removeOffConnection(savedInstanceState);
        }
    }


    private void verifyLocation(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Snackbar.make(mBottomConatainer, "Gps turned Off", BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.white))
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).show();

        }else {
            initializeGps();
        }
    }

    /*
    builds Uri to insert firebasekey
    */
    private String buildUri(String fkey) {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String host = getResources().getString(R.string.urlServer);
        String updateUser = getResources().getString(R.string.urlUserInsertFkey);
        String url = host + updateUser;

        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);

        String fkeyQuery = "fk";

        return Uri.parse(url).buildUpon().appendQueryParameter(fkeyQuery, fkey)
                .appendQueryParameter(uidQuery, uidValue).build().toString();
    }

    /*
    check if banned status and if firebase key exists on server
    */
    private void checkForKey(JSONArray array) throws JSONException {
        if (array.length() > 0) {
            if (array.length() > 0) {
                JSONObject object = array.getJSONObject(0);
                int banstatus = object.getInt("bstatus");
                if (banstatus == 1) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.singinBanned), Toast.LENGTH_LONG).show();
                    deleteAllUserEntity();
                    Intent intro = new Intent(StartActivity.this, IntroActivity.class);
                    intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intro);
                } else if (banstatus == 0) {
                    String firekey = object.getString("fkey");
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    if (firekey == null || firekey.equalsIgnoreCase("null")) {
                        if (refreshedToken != null) {
                            updateValues(refreshedToken);
                        }
                    } else if (!(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.prefFirebaseToken), mNullValue)).equalsIgnoreCase(mNullValue)) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getResources().getString(R.string.prefFirebaseToken), refreshedToken).apply();
                    }
                }
            }
        }
    }

    private void deleteAllUserEntity() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spf.edit().putBoolean(getResources().getString(R.string.prefAccountIndicator), false).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountId), mNullValue).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountName), mNullValue).apply();
        spf.edit().putString(getResources().getString(R.string.prefFirebaseToken), mNullValue).apply();
        spf.edit().putInt(getResources().getString(R.string.prefFirstOpen), 0).apply();
        deleteFile();
        getContentResolver().delete(CartTables.mCartContentUri, null, null);
    }

    private void deleteFile() {
        File folder = getExternalCacheDir();
        File f = new File(folder, "profile.jpg");
        if (f.exists()) {
            f.delete();
        }
    }


    /*
    returns uri for a single user
    */
    private String buildGetAllUser(String accId) {
        String host = getResources().getString(R.string.urlServer);
        String queryUserName = getResources().getString(R.string.urlUserQuery);
        String url = host + queryUserName;
        String uidQuery = "uid";
        return Uri.parse(url).buildUpon().appendQueryParameter(uidQuery, accId).build().toString();
    }

    /*
    get all values of a particular user
    */
    private void preFetchValues(String id) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildGetAllUser(id), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    checkForKey(response);
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

    /*
    updates firebasekey value on server
    */
    private void updateValues(final String key) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUri(key), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit().putString(getResources().getString(R.string.prefFirebaseToken), key).apply();
                Log.d("fkey", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("fkey", error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    /*
    remove this on connection
    */
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

    /*
    do this on plus click
    */
    private void fabClick() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (spf.getBoolean(getResources().getString(R.string.prefAccountIndicator), false)) {
            if(ifInCircle()){
                startActivityForResult(new Intent(StartActivity.this, AddBook.class), mAddBookRequestCode);
            }else {
                Toast.makeText(getApplicationContext(),"Sorry we don't have provide service in your area now we are continuously working hard to increase our coverage zone",Toast.LENGTH_LONG).show();
            }
        } else {
            checkFirst();
        }
    }

    /*
    function to check storage permission
    */
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE_CODE);
        } else {
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(spf.getString(getResources().getString(R.string.prefLatitude),mNullValue).equalsIgnoreCase(mNullValue)||
                    spf.getString(getResources().getString(R.string.prefLongitude),mNullValue).equalsIgnoreCase(mNullValue)){
                initializeGps();
                locateOnConnection();
            }else {
                fabClick();
            }
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

    /*
    click listener and g=fragment switch
    */
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
                        if (mFragmentBookPager != null) {
                            if (mFragmentBookPager.isHidden()) {
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.show(mFragmentBookPager);
                                ft.hide(mRequestListFragment);
                                ft.hide(mAccountFragment);
                                ft.hide(mMoreFragment);
                                ft.hide(myCartFragment);
                                bottomSelection(0);
                            }
                        }
                        break;
                    case R.id.bottomMenuCart:
                        if (myCartFragment != null) {
                            if (myCartFragment.isHidden()) {
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.hide(mFragmentBookPager);
                                ft.hide(mRequestListFragment);
                                ft.hide(mAccountFragment);
                                ft.hide(mMoreFragment);
                                ft.show(myCartFragment);
                                bottomSelection(2);
                            }
                        }
                        break;
                    case R.id.bottomMenuRequest:
                        if (mRequestListFragment != null) {
                            if (mRequestListFragment.isHidden()) {
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.hide(mFragmentBookPager);
                                ft.show(mRequestListFragment);
                                ft.hide(mAccountFragment);
                                ft.hide(mMoreFragment);
                                ft.hide(myCartFragment);
                                bottomSelection(1);
                            }
                        }
                        break;
                    case R.id.bottomMenuAccount:
                        if (mAccountFragment != null) {
                            if (mAccountFragment.isHidden()) {
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.hide(mFragmentBookPager);
                                ft.hide(mRequestListFragment);
                                ft.show(mAccountFragment);
                                ft.hide(mMoreFragment);
                                ft.hide(myCartFragment);
                                bottomSelection(3);
                            }
                        }
                        break;
                    case R.id.bottomMenuMore:
                        if (mMoreFragment != null) {
                            if (mMoreFragment.isHidden()) {
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.hide(mFragmentBookPager);
                                ft.hide(mRequestListFragment);
                                ft.hide(mAccountFragment);
                                ft.show(mMoreFragment);
                                ft.hide(myCartFragment);
                                bottomSelection(4);
                            }
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

    private boolean ifInCircle(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double fixedLatitude = Double.parseDouble(spf.getString(getResources().getString(R.string.longititude),mNullValue));
        double fixedLongitude = Double.parseDouble(spf.getString(getResources().getString(R.string.longititude),mNullValue));
        double myLatitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLatitude),mNullValue));
        double myLongitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLongitude),mNullValue));
        float[] results = new float[1];
        Location.distanceBetween(fixedLatitude, fixedLongitude, myLatitude, myLongitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < 5000;
    }


    private void initializeGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void locateOnConnection(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getResources().getString(R.string.prefLatitude),
                    String.valueOf(mLastLocation.getLatitude())).apply();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getResources().getString(R.string.prefLatitude),
                    String.valueOf(mLastLocation.getLongitude())).apply();

        }
    }

    public void onStart() {
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locateOnConnection();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

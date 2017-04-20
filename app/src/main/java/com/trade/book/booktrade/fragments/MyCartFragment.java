package com.trade.book.booktrade.fragments;


import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.trade.book.booktrade.PurchaseActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.StartActivity;
import com.trade.book.booktrade.adapters.adapterCart;
import com.trade.book.booktrade.cartData.CartTables;
import com.trade.book.booktrade.cartData.CartTables.tablecart;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentGetLocation;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.BookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.LOCATION_SERVICE;


public class MyCartFragment extends Fragment implements View.OnClickListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int mCartLoaderId = 2;
    private static final String mNullValue = "N/A";
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 56;
    @BindView(R.id.cartList)
    GridView bookCartGrid;
    @BindView(R.id.cartEmpty)
    ImageView noItem;
    @BindView(R.id.cartCheckOut)
    FloatingActionButton checkOut;
    adapterCart cartAdapter;
    private int mCursorCount = 0;
    private Unbinder mUnbinder;

    public MyCartFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_cart, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initilize();
        setEmpty();
        checkPrefrence();
        return v;
    }

    private void checkPrefrence() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefValue = spf.getString(getResources().getString(R.string.prefThemeKey), mNullValue);
        if (prefValue.equalsIgnoreCase("Default")) {
            noItem.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.emptycrt));
        } else if (prefValue.equalsIgnoreCase("Multi-Color")) {
            noItem.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.emptycrtcolor));
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getLoaderManager().initLoader(mCartLoaderId, null, this);
    }

    private void initilize() {
        cartAdapter = new adapterCart(getActivity(), null);
        checkOut.setEnabled(false);
        bookCartGrid.setAdapter(cartAdapter);
        checkOut.setOnClickListener(this);
        bookCartGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if (c.moveToPosition(position)) {
                    BookObject bookObject = new BookObject(c.getInt(c.getColumnIndex(tablecart.mBuid)),
                            c.getString(c.getColumnIndex(tablecart.mName)),
                            c.getString(c.getColumnIndex(tablecart.mPublisher)),
                            c.getInt(c.getColumnIndex(tablecart.mCostPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mSellingPrice)),
                            c.getInt(c.getColumnIndex(tablecart.mEdition)),
                            c.getString(c.getColumnIndex(tablecart.mDescription)),
                            c.getString(c.getColumnIndex(tablecart.mCondition)),
                            c.getString(c.getColumnIndex(tablecart.mCateogory)),
                            c.getString(c.getColumnIndex(tablecart.mUserId)),
                            c.getInt(c.getColumnIndex(tablecart._ID))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto0))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto1))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto2))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto3))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto4))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto5))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto6))
                            , c.getString(c.getColumnIndex(tablecart.mPhoto7))
                            , c.getInt(c.getColumnIndex(tablecart.mstatus)));
                    Intent detail = new Intent(getActivity(), PurchaseActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(getResources().getString(R.string.intenKeyObejct), bookObject);
                    detail.putExtras(b);
                    detail.putExtra(getResources().getString(R.string.intenfromcart), true);
                    startActivity(detail);
                }
            }
        });
    }

    private void setEmpty() {
        if (getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null).getCount() <= 0) {
            checkOut.setEnabled(false);
            noItem.setVisibility(View.VISIBLE);
        } else {
            noItem.setVisibility(View.GONE);
            checkOut.setEnabled(true);
        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case mCartLoaderId:
                return new android.content.CursorLoader(getActivity(), CartTables.mCartContentUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        setEmpty();
        cartAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        cartAdapter.swapCursor(null);
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
            Toast.makeText(getActivity(), "Visible", Toast.LENGTH_SHORT).show();
        }

        super.setMenuVisibility(visible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartCheckOut:
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (spf.getString(getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue) ||
                        spf.getString(getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
                    checkLocation();
                } else {
                    if (checkStatus() || checkVelloreStatus()) {
                        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
                        if (c.getCount() > 0) {
                            checkSold();
                        } else {
                            Toast.makeText(getActivity(), "Cart Empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "We are sorry your region doesn't fall into " +
                                "our coverage zone, we are continuously working hard to expand our coverage zone", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "If you think its a mistake try recalibrating location from more ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                buildAlertMessageNoGps();
            } else {
                dialogFragmentGetLocation getLocation = new dialogFragmentGetLocation();
                getLocation.show(getFragmentManager(), "location");
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double sLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.latitude));
        double sLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.longititude));
        double myLatitude = 0.0;
        double myLongitude = 0.0;
        int count = 0;
        if (!spf.getString(getActivity().getResources().getString(R.string.prefLatitude), mNullValue).equalsIgnoreCase(mNullValue)) {
            count++;
            myLatitude = Double.parseDouble(spf.getString(getResources().getString(R.string.prefLatitude), mNullValue));
        }
        if (!spf.getString(getActivity().getResources().getString(R.string.prefLongitude), mNullValue).equalsIgnoreCase(mNullValue)) {
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
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double vLatitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLatitude));
        double vLongitude = Double.parseDouble(getActivity().getResources().getString(R.string.velloreLongititude));
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

    private double compute(int price) {
        if (price < 100) {
            return 7;
        }
        if (price >= 100 && price < 300) {
            return ((double) 8 / 100) * price;
        }
        if (price >= 300 && price <= 999) {
            return ((double) 6 / 100) * price;
        }
        if (price > 999) {
            return ((double) 4 / 100) * price;
        }
        return 0;
    }

    private void buildCheckOutDialog(String message) {
        final AlertDialog.Builder abd = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        abd.setTitle("Check Out");
        abd.setMessage(message);
        abd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                uploadigDialog().show();
                checkOutFromCart();
            }
        });
        dialog = abd.create();
        dialog.show();
    }

    private Dialog uploadigDialog() {
        AlertDialog.Builder uploading = new AlertDialog.Builder(getActivity());
        uploading.setMessage("Processing...").setCancelable(false).create().show();
        return uploading.create();
    }

    private void checkOutFromCart() {
        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        while (c.moveToNext()) {
            buy(c);
            shift(c);
        }
        Toast.makeText(getActivity(), "Purchase Successful", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                uploadigDialog().dismiss();
                getActivity().getContentResolver().delete(CartTables.mCartContentUri, null, null);
                sendNotification();
                getActivity().finish();
                startActivity(new Intent(getActivity(), StartActivity.class));
            }
        }, 2000);
    }

    private String buildUpdateUri(Cursor cursor) {
        String host = getResources().getString(R.string.urlServer);
        String moveToTransit = getResources().getString(R.string.urlMoveToTransit);
        String url = host + moveToTransit;

        String bidQuery = "id";

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(bidQuery, String.valueOf(cursor.getInt(cursor.getColumnIndex(tablecart.mBuid))))
                .build().toString();
    }

    private String buildTransactionUri(Cursor cursor) {

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String host = getResources().getString(R.string.urlServer);

        String insertTransaction = getResources().getString(R.string.urlTransactionInsert);

        String url = host + insertTransaction;

        Calendar c = Calendar.getInstance();

        String buyerUidQuery = "buid";
        String buyerUidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);

        String sellerUidQuery = "suid";
        String sellerUidValue = cursor.getString(cursor.getColumnIndex(tablecart.mUserId));

        int price = cursor.getInt(cursor.getColumnIndex(tablecart.mSellingPrice));

        String buyerMoneyQuery = "bp";
        String buyerMoneyValue = ((price + compute(price)) + "");

        String sellerMoneyQuery = "sp";
        String sellerMoneyValue = ((price - compute(price)) + "");

        String dateQuery = "bt";
        String dateValue = String.valueOf(c.getTimeInMillis());

        String bookIdQuery = "bkid";
        int bookIdValue = cursor.getInt(cursor.getColumnIndex(tablecart.mBuid));

        String statusQuery = "st";

        return Uri.parse(url).buildUpon()
                .appendQueryParameter(buyerUidQuery, buyerUidValue)
                .appendQueryParameter(sellerUidQuery, sellerUidValue)
                .appendQueryParameter(buyerMoneyQuery, buyerMoneyValue)
                .appendQueryParameter(sellerMoneyQuery, sellerMoneyValue)
                .appendQueryParameter(dateQuery, dateValue)
                .appendQueryParameter(bookIdQuery, String.valueOf(bookIdValue))
                .appendQueryParameter(statusQuery, String.valueOf(0))
                .build().toString();
    }

    private void buy(Cursor cursor) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildTransactionUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
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

    private void removeSold(final int bid, final int count, final Dialog d) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildSoldUri(bid), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    removeIfAvailable(response, bid, count, d);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void removeIfAvailable(JSONArray status, int id, int count, Dialog d) throws JSONException {
        if (status.length() > 0) {
            JSONObject obj = status.getJSONObject(0);
            int statusCode = obj.getInt("status");
            mCursorCount++;
            if (statusCode == 1) {
                getActivity().getContentResolver().delete(Uri.withAppendedPath(CartTables.mCartContentUri, String.valueOf(id)), null, null);
                Toast.makeText(getActivity(), "Item removed from cart as it was sold", Toast.LENGTH_SHORT).show();
            }
        }
        if (mCursorCount == count) {
            Cursor c1 = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
            mCursorCount = 0;
            d.dismiss();
            if (c1.getCount() > 0) {
                showCheckOutDialog();
            } else {
                Toast.makeText(getActivity(), "Cart Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSold() {
        final Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        AlertDialog.Builder wait = new AlertDialog.Builder(getActivity());
        wait.setMessage("Verifying");
        final Dialog d = wait.create();
        d.show();
        while (c.moveToNext()) {
            removeSold(c.getInt(c.getColumnIndex(tablecart.mBuid)), c.getCount(), d);
        }
    }

    private void showCheckOutDialog() {
        Cursor c = getActivity().getContentResolver().query(CartTables.mCartContentUri, null, null, null, null);
        int sp = 0;
        while (c.moveToNext()) {
            sp = sp + c.getInt(c.getColumnIndex(tablecart.mSellingPrice));
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double taxPrice = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", compute(sp)));
        taxPrice = Math.round(taxPrice);
        buildCheckOutDialog("All the books left in your cart will delivered to " +
                spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress), mNullValue) + " within one week\n\n" +
                "Total Price    : " + "र " + (double) sp + "\n"
                + "\n"
                + "Convenience Fee     : " + "र " + taxPrice + "\n"
                + "\n"
                + "Final Amount   : " + "र " + (sp + taxPrice));
    }

    private void sendNotification() {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Order Confirmed")
                .setContentText("All item will be delivered within a week")
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void shift(Cursor cursor) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(cursor), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        }

        if (animation != null) {
            getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    getView().setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });
        }

        return animation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

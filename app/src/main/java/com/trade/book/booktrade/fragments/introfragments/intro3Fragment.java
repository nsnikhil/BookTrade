package com.trade.book.booktrade.fragments.introfragments;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.paolorotolo.appintro.ISlidePolicy;
import com.trade.book.booktrade.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class intro3Fragment extends Fragment implements ISlidePolicy {

    boolean hasExtra = false;
    EditText phone,address;
    private static final String mNullValue = "N/A";

    public intro3Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro3, container, false);
        initilize(v);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(!spf.getString(getResources().getString(R.string.prefAccountId),mNullValue).equalsIgnoreCase(mNullValue)){
            preFetchValues();
        }
        return v;
    }

    private void initilize(View v) {
        phone = (EditText)v.findViewById(R.id.intro3Phoneno);
        address = (EditText)v.findViewById(R.id.intro3Address);
    }

    private String  buildSetUri(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String queryUserName = getResources().getString(R.string.urlUserQuery);
        String url = host+queryUserName;
        String uidQuery = "uid";
        return Uri.parse(url).buildUpon().appendQueryParameter(uidQuery,spf.getString(getResources().getString(R.string.prefAccountId),mNullValue)).build().toString();
    }

    private void preFetchValues(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildSetUri(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    setValues(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void setValues(JSONArray response) throws JSONException {
        if(response.length()>0){
            JSONObject object  = response.getJSONObject(0);
            phone.setText(object.getString("phoneno"));
            address.setText(object.getString("address"));
        }
    }


    private boolean verify(){
        if(phone.getText().toString().isEmpty()||phone.getText().toString().length()==0){
            Toast.makeText(getActivity(),"Enter Phone No",Toast.LENGTH_SHORT).show();
            return false;
        }if(address.getText().toString().isEmpty()||address.getText().toString().length()==0){
            Toast.makeText(getActivity(),"Enter Address",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveValues(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spf.edit().putString(getResources().getString(R.string.prefAccountPhone),phone.getText().toString()).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountAddress),address.getText().toString()).apply();
        checkExist();
    }

    private String budilUri(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String insertUser = getResources().getString(R.string.urlUserInsert);
        String url = host + insertUser;

        String nameQuery = "nm";
        String nameValue = spf.getString(getResources().getString(R.string.prefAccountName),mNullValue);

        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId),mNullValue);

        String phoneQuery = "ph";
        String phoneValue = spf.getString(getResources().getString(R.string.prefAccountPhone),mNullValue);

        String addressQuery = "adr";
        String adddressValue = spf.getString(getResources().getString(R.string.prefAccountAddress),mNullValue);

        return Uri.parse(url).buildUpon().appendQueryParameter(nameQuery,nameValue)
                .appendQueryParameter(uidQuery,uidValue)
                .appendQueryParameter(phoneQuery,phoneValue)
                .appendQueryParameter(addressQuery,adddressValue).build().toString();
    }

    private String buildUpdateUri(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String updateUser = getResources().getString(R.string.urlUserUpdate);
        String url = host + updateUser;

        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId),mNullValue);

        String phoneQuery = "ph";
        String phoneValue = phone.getText().toString();

        String addressQuery = "adr";
        String adddressValue = address.getText().toString();

        return Uri.parse(url).buildUpon().appendQueryParameter(uidQuery,uidValue)
                .appendQueryParameter(phoneQuery,phoneValue)
                .appendQueryParameter(addressQuery,adddressValue).build().toString();
    }

    private String budilCheckUri() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String host = getResources().getString(R.string.urlServer);
        String checkUser = getResources().getString(R.string.urlUserQuery);
        String url = host + checkUser;
        String uidQuery = "uid";
        String uidValue = spf.getString(getResources().getString(R.string.prefAccountId), mNullValue);
        return Uri.parse(url).buildUpon().appendQueryParameter(uidQuery, uidValue).build().toString();
    }

    private void checkExist(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, budilCheckUri(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               if(response.length()==0){
                   insertIntoDatabase();
               }else {
                   upadteValues();
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void upadteValues(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(),"Values Updated",Toast.LENGTH_SHORT).show();
                hasExtra = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void insertIntoDatabase(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, budilUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                hasExtra = true;
                Toast.makeText(getActivity(),"Swipe Left",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean isPolicyRespected() {
        return hasExtra;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        if(verify()){
            saveValues();
        }
    }
}

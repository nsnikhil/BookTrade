package com.trade.book.booktrade.fragments.dialogfragments;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class dialogFagmentAddress extends DialogFragment {

    @BindView(R.id.dialogAddress) TextInputEditText address;
    @BindView(R.id.dialogPhone) TextInputEditText phone;
    @BindView(R.id.dialogSave) Button save;
    private static final String mNullValue = "N/A";

    public dialogFagmentAddress() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fagment_address, container, false);
        ButterKnife.bind(this,v);
        initilize();
        setValue();
        return v;
    }

    private void initilize() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                spf.edit().putString(getResources().getString(R.string.prefAccountPhone),phone.getText().toString()).apply();
                spf.edit().putString(getResources().getString(R.string.prefAccountAddress),address.getText().toString()).apply();
                upadteValues();
            }
        });
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            save.setTextColor(getResources().getColor(R.color.cardview_dark_background));
        }
    }

    private void setValue(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        address.setText(spf.getString(getResources().getString(R.string.prefAccountAddress),mNullValue));
        phone.setText(spf.getString(getResources().getString(R.string.prefAccountPhone),mNullValue));
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

    private void upadteValues(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUpdateUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(),"Values Updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

}

package com.trade.book.booktrade.fragments.dialogfragments;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.interfaces.interfaceAddRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class dialogFragmentRequest extends DialogFragment {

    private static final String mNullValue = "N/A";
    @BindView(R.id.requestName)
    TextInputEditText name;
    @BindView(R.id.requestPublisher)
    TextInputEditText publisher;
    @BindView(R.id.requestSave)
    Button save;
    interfaceAddRequest addRequest;
    private Unbinder mUnbinder;


    public dialogFragmentRequest() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fragment_request, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initilize();
        addRequest = (interfaceAddRequest) getTargetFragment();
        return v;
    }

    private void initilize() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verify()) {
                    addToRequest();
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            save.setTextColor(ContextCompat.getColor(getActivity(), R.color.cardview_dark_background));
        }
    }

    private String buildRequestUri() {
        String server = getActivity().getResources().getString(R.string.urlServer);
        String requestAdd = getActivity().getResources().getString(R.string.urlRequestInsert);
        String url = server + requestAdd;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String nameQuery = "nm";
        String nameValue = name.getText().toString();
        String publiserQuery = "pb";
        String publisherValue = publisher.getText().toString();
        String uidQuery = "uid";
        String uidValue = spf.getString(getActivity().getResources().getString(R.string.prefAccountId), mNullValue);
        return Uri.parse(url).buildUpon().appendQueryParameter(nameQuery, nameValue)
                .appendQueryParameter(publiserQuery, publisherValue)
                .appendQueryParameter(uidQuery, uidValue).build().toString();

    }

    private void addToRequest() {
        RequestQueue requestObject = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildRequestUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismiss();
                addRequest.refreshList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestObject.add(stringRequest);
    }

    private boolean verify() {
        if (name.getText().toString().isEmpty() || name.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter the name of the book", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (publisher.getText().toString().isEmpty() || publisher.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter the name of publisher", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

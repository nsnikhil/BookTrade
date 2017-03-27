package com.trade.book.booktrade.fragments.dialogfragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trade.book.booktrade.R;


public class dialogFragmentRequest extends DialogFragment {

    EditText name,publisher;
    Button save;

    public dialogFragmentRequest() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dialog_fragment_request, container, false);
        initilize(v);
        return v;
    }

    private void initilize(View v) {
        name = (EditText)v.findViewById(R.id.requestName);
        publisher = (EditText)v.findViewById(R.id.requestPublisher);
        save = (Button)v.findViewById(R.id.requestSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Will add to request",Toast.LENGTH_SHORT).show();
            }
        });
    }

}

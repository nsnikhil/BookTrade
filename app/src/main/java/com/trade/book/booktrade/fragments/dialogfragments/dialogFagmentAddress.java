package com.trade.book.booktrade.fragments.dialogfragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trade.book.booktrade.R;


public class dialogFagmentAddress extends DialogFragment {


    public dialogFagmentAddress() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_fagment_address, container, false);
    }

}

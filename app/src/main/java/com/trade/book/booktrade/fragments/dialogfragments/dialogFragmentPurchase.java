package com.trade.book.booktrade.fragments.dialogfragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.book.booktrade.R;


public class dialogFragmentPurchase extends DialogFragment {

    TextView title,actualPrice,extraPrice,totalPrice;
    private static final String mNullValue = "N/A";

    public dialogFragmentPurchase() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dialog_fragment_purchase, container, false);
        initilize(v);
        setVal();
        return v;
    }

    private void initilize(View v) {
        title = (TextView)v.findViewById(R.id.dialogPurchaseHead);
        actualPrice = (TextView)v.findViewById(R.id.dialogPurchasePrice);
        extraPrice = (TextView)v.findViewById(R.id.dialogPurchaseExtra);
        totalPrice = (TextView)v.findViewById(R.id.dialogPurchaseTotal);
    }

    private void setVal(){
        Bundle args = getArguments();
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = args.getString(getActivity().getResources().getString(R.string.bundleBookName));
        String publisher = args.getString(getActivity().getResources().getString(R.string.bundleBookPublisher));
        int price = args.getInt(getActivity().getResources().getString(R.string.bundleBookPrice));
        title.setText(name+" by "+publisher+ " will be delivered to "+spf.getString(getActivity().getResources().getString(R.string.prefAccountAddress),mNullValue)+" within one week");
        actualPrice.setText((double)price+"");
        extraPrice.setText(compute(price)+"");
        totalPrice.setText((price+compute(price))+"");
    }

    private double compute(int price){
        if(price>0&&price<300){
            return  ((double) 8/100)*price;
        }if(price>=300&&price<=999){
            return  ((double) 6/100)*price;
        }if(price>999){
            return  ((double) 4/100)*price;
        }
        return 0;
    }

}

package com.trade.book.booktrade;

import android.app.Dialog;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.trade.book.booktrade.fragments.*;

public class PrefActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar prefToolbar;
    RelativeLayout aboutContainer;
    LinearLayout prefContainer;
    Button devs,terms,license,libraries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        initilize();
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getInt(getResources().getString(R.string.intentExtraPrefrence))==3002){
                prefContainer.setVisibility(View.VISIBLE);
                aboutContainer.setVisibility(View.GONE);
                getFragmentManager().beginTransaction().add(R.id.prefContainer,new prefFragment()).commit();
            }
            if(getIntent().getExtras().getInt(getResources().getString(R.string.intentExtraAbout))==3003){
                aboutContainer.setVisibility(View.VISIBLE);
                prefContainer.setVisibility(View.GONE);
                getSupportActionBar().setTitle("About");
            }
        }
    }

    private void initilize() {
        prefToolbar = (Toolbar)findViewById(R.id.toolbarPref);
        setSupportActionBar(prefToolbar);
        aboutContainer = (RelativeLayout)findViewById(R.id.aboutContainer);
        prefContainer = (LinearLayout)findViewById(R.id.prefContainer);
        devs =  (Button)findViewById(R.id.aboutButtonDevs);
        terms = (Button)findViewById(R.id.aboutButtonTerms);
        license = (Button)findViewById(R.id.aboutButtonLicense);
        libraries = (Button)findViewById(R.id.aboutButtonLibraries);
        devs.setOnClickListener(this);
        terms.setOnClickListener(this);
        license.setOnClickListener(this);
        libraries.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aboutButtonDevs:
                aboutDialog("Akash Surana \n\nAyush Lodha \n\nNikhil Soni");
                break;
            case R.id.aboutButtonTerms:
                aboutDialog(getResources().getString(R.string.condition));
                break;
            case R.id.aboutButtonLicense:
                aboutDialog(getResources().getString(R.string.copyright));
                break;
            case R.id.aboutButtonLibraries:
                aboutDialog("Will show all the 3rd party libraries used");
                break;
        }
    }

    private Dialog aboutDialog(String message){
        AlertDialog.Builder abt = new AlertDialog.Builder(PrefActivity.this)
                .setMessage(message);
        Dialog d = abt.create();
        abt.show();
        return d;
    }

    public static class prefFragment extends PreferenceFragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
    }

}

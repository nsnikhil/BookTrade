package com.trade.book.booktrade;

import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class PrefActivity extends AppCompatActivity {

    Toolbar prefToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        initilize();
        getFragmentManager().beginTransaction().add(R.id.prefContainer,new prefrag()).commit();
    }

    private void initilize() {
        prefToolbar = (Toolbar)findViewById(R.id.toolbarPref);
        setSupportActionBar(prefToolbar);
    }

    public static class prefrag extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
    }
}

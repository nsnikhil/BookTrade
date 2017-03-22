package com.trade.book.booktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.trade.book.booktrade.fragments.*;

public class PrefActivity extends AppCompatActivity {

    Toolbar prefToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        initilize();
        getSupportFragmentManager().beginTransaction().add(R.id.prefContainer,new fragmentMore()).commit();
    }

    private void initilize() {
        prefToolbar = (Toolbar)findViewById(R.id.toolbarPref);
        setSupportActionBar(prefToolbar);
        getSupportActionBar().setTitle("More");
    }

}

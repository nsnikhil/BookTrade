package com.trade.book.booktrade;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
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

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.leakcanary.LeakCanary;
import com.trade.book.booktrade.fragments.*;

public class PrefActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar prefToolbar;
    RelativeLayout aboutContainer;
    LinearLayout prefContainer;
    Button devs, terms, license, libraries;
    ShimmerTextView mShimmerTextView;
    Shimmer shimmer = new Shimmer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_pref);
        initilize();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt(getResources().getString(R.string.intentExtraPrefrence)) == 3002) {
                prefContainer.setVisibility(View.VISIBLE);
                aboutContainer.setVisibility(View.GONE);
                prefToolbar.setVisibility(View.VISIBLE);
                getFragmentManager().beginTransaction().add(R.id.prefContainer, new prefFragment()).commit();
            }
            if (getIntent().getExtras().getInt(getResources().getString(R.string.intentExtraAbout)) == 3003) {
                aboutContainer.setVisibility(View.VISIBLE);
                prefContainer.setVisibility(View.GONE);
                prefToolbar.setVisibility(View.GONE);
            }
        }
    }

    private void initilize() {
        prefToolbar = (Toolbar) findViewById(R.id.toolbarPref);
        setSupportActionBar(prefToolbar);
        aboutContainer = (RelativeLayout) findViewById(R.id.aboutContainer);
        prefContainer = (LinearLayout) findViewById(R.id.prefContainer);
        mShimmerTextView = (ShimmerTextView)findViewById(R.id.aboutText);
        shimmer.setDuration(6000).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        devs = (Button) findViewById(R.id.aboutButtonDevs);
        terms = (Button) findViewById(R.id.aboutButtonTerms);
        license = (Button) findViewById(R.id.aboutButtonLicense);
        libraries = (Button) findViewById(R.id.aboutButtonLibraries);
        devs.setOnClickListener(this);
        terms.setOnClickListener(this);
        license.setOnClickListener(this);
        libraries.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                aboutDialog("Clans-FloatingActionButton" +
                        "\n\nlopspower-CircularImageView" +
                        "\n\napl-devs-AppIntro" +
                        "\n\nlapism-SearchView"+
                        "\n\nFacebook SDK for Android" +
                        "\n\nAWS Mobile SDK for Android" +
                        "\n\nbumptech-glide" +
                        "\n\nclaudiodegio-MsvSearch" +
                        "\n\nRomainPiel-Shimmer-android" +
                        "\n\nwasabeef-Blurry" +
                        "\n\nKeepSafe-TapTargetView" +
                        "\n\nsquare-leakcanary" +
                        "\n\nJakeWharton-butterknife");
                break;
        }
    }

    private Dialog aboutDialog(String message) {
        AlertDialog.Builder abt = new AlertDialog.Builder(PrefActivity.this)
                .setMessage(message);
        Dialog d = abt.create();
        abt.show();
        return d;
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmer.start(mShimmerTextView);
    }

    public static class prefFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
    }

}

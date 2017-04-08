package com.trade.book.booktrade;

import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrefActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbarPref) Toolbar prefToolbar;
    @BindView(R.id.aboutContainer) RelativeLayout aboutContainer;
    @BindView(R.id.prefContainer) LinearLayout prefContainer;
    @BindView(R.id.aboutButtonDevs) Button devs;
    @BindView(R.id.aboutButtonTerms) Button terms;
    @BindView(R.id.aboutButtonLicense) Button license;
    @BindView(R.id.aboutButtonLibraries) Button libraries;
    @BindView(R.id.aboutText) ShimmerTextView mShimmerTextView;
    @BindView(R.id.aboutVersion) TextView mVersionText;
    Shimmer shimmer = new Shimmer();

    String[] libraryName = { "Clans-FloatingActionButton","lopspower-CircularImageView","apl-devs-AppIntro","lapism-SearchView","Facebook SDK for Android","AWS Mobile SDK for Android",
            "bumptech-glide","claudiodegio-MsvSearch","RomainPiel-Shimmer-android","wasabeef-Blurry","KeepSafe-TapTargetView","square-leakcanary","JakeWharton-butterknife"};

    String[] libraryLink = {"https://github.com/Clans/FloatingActionButton","https://github.com/lopspower/CircularImageView","https://github.com/apl-devs/AppIntro"
            ,"https://github.com/lapism/SearchView","https://developers.facebook.com/docs/android","https://aws.amazon.com/mobile/sdk/","https://github.com/bumptech/glide"
            ,"https://github.com/claudiodegio/MsvSearch","https://github.com/RomainPiel/Shimmer-android","https://github.com/wasabeef/Blurry"
            ,"https://github.com/KeepSafe/TapTargetView","https://github.com/square/leakcanary","https://github.com/JakeWharton/butterknife"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        setContentView(R.layout.activity_pref);
        ButterKnife.bind(this);
        initialize();
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

    private void initialize() {
        setSupportActionBar(prefToolbar);
        shimmer.setDuration(6000).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        PackageManager manager = getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            mVersionText.setText(getResources().getString(R.string.aboutVersion)+" : "+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
                showLibrariesList();
                break;
        }
    }

    private void showLibrariesList() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(PrefActivity.this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PrefActivity.this, android.R.layout.simple_list_item_1);
        for(int i=0;i<libraryName.length;i++){
            arrayAdapter.add(libraryName[i]);
        }
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                openLink(libraryLink[position]);
            }
        });
        choosePath.create().show();
    }

    private void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        if(i.resolveActivity(getPackageManager())!=null){
            startActivity(i);
        }else {
            Toast.makeText(getApplicationContext(),"You don't have a browser app to visit the link",Toast.LENGTH_SHORT).show();
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

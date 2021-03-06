package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentMyInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrefActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String termsUrl = "https://docs.google.com/document/d/1a67czBVEUSpL0u8DCByhEbEG1cTVyB8mvC9eLo3Jt6M/pub";
    @BindView(R.id.toolbarPref)
    Toolbar prefToolbar;
    @BindView(R.id.aboutContainer)
    RelativeLayout aboutContainer;
    @BindView(R.id.prefContainer)
    LinearLayout prefContainer;
    @BindView(R.id.aboutButtonDevs)
    Button devs;
    @BindView(R.id.aboutButtonTerms)
    Button terms;
    @BindView(R.id.aboutButtonLibraries)
    Button libraries;
    @BindView(R.id.aboutText)
    ShimmerTextView mShimmerTextView;
    @BindView(R.id.aboutVersion)
    TextView mVersionText;
    Shimmer shimmer = new Shimmer();
    String[] libraryName = {"Clans-FloatingActionButton", "lopspower-CircularImageView", "apl-devs-AppIntro", "lapism-SearchView", "Facebook SDK for Android", "AWS Mobile SDK for Android",
            "bumptech-glide", "claudiodegio-MsvSearch", "RomainPiel-Shimmer-android", "wasabeef-Blurry", "KeepSafe-TapTargetView", "square-leakcanary", "JakeWharton-butterknife"};

    String[] libraryLink = {"https://github.com/Clans/FloatingActionButton", "https://github.com/lopspower/CircularImageView", "https://github.com/apl-devs/AppIntro"
            , "https://github.com/lapism/SearchView", "https://developers.facebook.com/docs/android", "https://aws.amazon.com/mobile/sdk/", "https://github.com/bumptech/glide"
            , "https://github.com/claudiodegio/MsvSearch", "https://github.com/RomainPiel/Shimmer-android", "https://github.com/wasabeef/Blurry"
            , "https://github.com/KeepSafe/TapTargetView", "https://github.com/square/leakcanary", "https://github.com/JakeWharton/butterknife"};
    private int mCount = 0;

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
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initialize() {
        setSupportActionBar(prefToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shimmer.setDuration(6000).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        PackageManager manager = getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            mVersionText.setText(getResources().getString(R.string.aboutVersion) + " : " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        devs.setOnClickListener(this);
        terms.setOnClickListener(this);
        libraries.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutButtonDevs:
                if (mCount == 4) {
                    mCount = 0;
                    dialogFragmentMyInfo dialogFragmentMyInfo = new dialogFragmentMyInfo();
                    dialogFragmentMyInfo.show(getSupportFragmentManager(), "myinfo");
                } else {
                    mCount++;
                    aboutDialog();
                }
                break;
            case R.id.aboutButtonTerms:
                chromeCustomTab(termsUrl);
                break;
            case R.id.aboutButtonLibraries:
                showLibrariesList();
                break;
        }
    }

    private void chromeCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private void showLibrariesList() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(PrefActivity.this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PrefActivity.this, android.R.layout.simple_list_item_1);
        for (String aLibraryName : libraryName) {
            arrayAdapter.add(aLibraryName);
        }
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                chromeCustomTab(libraryLink[position]);
                //openLink(libraryLink[position]);
            }
        });
        choosePath.create().show();
    }

    private void aboutDialog() {
        AlertDialog.Builder abt = new AlertDialog.Builder(PrefActivity.this)
                .setMessage(getResources().getString(R.string.app_name));
        abt.create().show();
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
            ListPreference lp = (ListPreference) findPreference(getActivity().getResources().getString(R.string.prefThemeKey));
            lp.setSummary(lp.getEntry().toString());
            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), "Please restart the app to see the changes", Toast.LENGTH_SHORT).show();
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });
        }

    }

}

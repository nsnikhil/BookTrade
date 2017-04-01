package com.trade.book.booktrade;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.trade.book.booktrade.fragments.introfragments.intro1Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro2Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro3Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro4Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro5Fragment;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new intro1Fragment());
        addSlide(new intro2Fragment());
        addSlide(new intro5Fragment());
        addSlide(new intro3Fragment());
        addSlide(new intro4Fragment());
        showSkipButton(false);
        setProgressButtonEnabled(false);
        setIndicatorColor(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.cardview_dark_background));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        Intent mainStart = new Intent(IntroActivity.this, StartActivity.class);
        mainStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainStart);
    }

}

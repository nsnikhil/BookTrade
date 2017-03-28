package com.trade.book.booktrade;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.trade.book.booktrade.fragments.introfragments.intro1Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro2Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro3Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro4Fragment;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new intro1Fragment());
        addSlide(new intro2Fragment());
        addSlide(new intro3Fragment());
        addSlide(new intro4Fragment());
        showSkipButton(false);
        setProgressButtonEnabled(false);
        setVibrate(true);
        setVibrateIntensity(10);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        startActivity(new Intent(IntroActivity.this,MainActivity.class));
    }


}

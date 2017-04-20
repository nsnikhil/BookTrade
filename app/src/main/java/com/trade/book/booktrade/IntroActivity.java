package com.trade.book.booktrade;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.trade.book.booktrade.fragments.introfragments.intro1Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro2Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro3Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro4Fragment;
import com.trade.book.booktrade.fragments.introfragments.intro5Fragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        addSlide(new intro1Fragment());
        addSlide(new intro2Fragment());
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            addSlide(new intro5Fragment());
        }
        addSlide(new intro3Fragment());
        addSlide(new intro4Fragment());
        showSkipButton(false);
        setSwipeLock(true);
        setProgressButtonEnabled(true);
        setNextArrowColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        setSeparatorColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        setColorDoneText(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        setIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.cardview_dark_background));
    }

}

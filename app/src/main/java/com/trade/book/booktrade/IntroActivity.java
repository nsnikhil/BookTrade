package com.trade.book.booktrade;


import android.os.Bundle;
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
        addSlide(new intro5Fragment());
        addSlide(new intro3Fragment());
        addSlide(new intro4Fragment());
        showSkipButton(false);
        setSwipeLock(true);
        setProgressButtonEnabled(true);
        setNextArrowColor(getResources().getColor(R.color.colorAccent));
        setSeparatorColor(getResources().getColor(android.R.color.transparent));
        setColorDoneText(getResources().getColor(R.color.colorAccent));
        setIndicatorColor(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.cardview_dark_background));
    }

}

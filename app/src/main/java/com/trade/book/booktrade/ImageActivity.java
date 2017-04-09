package com.trade.book.booktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.imageFullScreen) ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(getIntent().getExtras()!=null){
            String url = getIntent().getExtras().getString(getResources().getString(R.string.intentImageUrl));
            Glide.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.back)
                    .crossFade()
                    .into(fullImage);
        }
    }
}

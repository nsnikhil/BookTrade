package com.trade.book.booktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {

    ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        fullImage = (ImageView)findViewById(R.id.imageFullScreen);
        if(getIntent().getExtras()!=null){
            String url = getIntent().getExtras().getString(getResources().getString(R.string.intentImageUrl));
            Glide.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.bookback)
                    .crossFade()
                    .into(fullImage);
        }
    }
}

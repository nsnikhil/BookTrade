package com.trade.book.booktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.adapters.adapterPurchaseImage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    //@BindView(R.id.imageFullScreen) ImageView fullImage;
    //@BindView(R.id.imageFullScreenProgress) ProgressBar loading;
    @BindView(R.id.imageFullScreenImages) RecyclerView imageList;
    adapterPurchaseImage imageAdapter;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        imageList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        urls = new ArrayList<>();
        if(getIntent()!=null){
            urls = getIntent().getStringArrayListExtra(getResources().getString(R.string.intentArrayListUrl));
            imageAdapter = new adapterPurchaseImage(ImageActivity.this,urls,2);
            imageList.setAdapter(imageAdapter);
            imageList.getLayoutManager().scrollToPosition(getIntent().getExtras().getInt(getResources().getString(R.string.intentArrayListPosition)));
        }
    }
}

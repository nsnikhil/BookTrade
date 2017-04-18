package com.trade.book.booktrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.trade.book.booktrade.adapters.adapterPurchaseImage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.imageFullScreenImages)
    RecyclerView imageList;
    @BindView(R.id.imageToolbar)
    Toolbar mImageToolbar;
    adapterPurchaseImage imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setSupportActionBar(mImageToolbar);
        mImageToolbar.setPadding(0, 52, 0, 0);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fill_transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<String> urls;
        if (getIntent() != null) {
            urls = getIntent().getStringArrayListExtra(getResources().getString(R.string.intentArrayListUrl));
            imageAdapter = new adapterPurchaseImage(ImageActivity.this, urls, 2);
            imageList.setAdapter(imageAdapter);
            imageList.getLayoutManager().scrollToPosition(getIntent().getExtras().getInt(getResources().getString(R.string.intentArrayListPosition)));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}

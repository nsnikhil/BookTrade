package com.trade.book.booktrade.fragments.dialogfragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.R;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class dialogFragmentMyInfo extends DialogFragment {


    private static final String mNrsImageUrl = "https://s3-ap-northeast-1.amazonaws.com/shelfbeecategory/nrs.png";
    private static final String mMyImageUrl = "https://s3-ap-northeast-1.amazonaws.com/shelfbeecategory/my.jpg";
    private static final int mPassword = 3103;
    @BindView(R.id.myNrsImage)
    ImageView mNrsImage;
    @BindView(R.id.myPImage)
    ImageView mMyImage;
    @BindView(R.id.myNrsText)
    TextView mNrsText;
    @BindView(R.id.myPTEXT)
    TextView mMyText;
    @BindView(R.id.myNrsImageProgress)
    ProgressBar mNrsImageProgress;
    @BindView(R.id.myPImageProgress)
    ProgressBar mMyImageProgress;
    @BindView(R.id.myImageContainer)
    LinearLayout mImageContainer;
    @BindView(R.id.myPasswordContainer)
    LinearLayout mPasswordContainer;
    @BindView(R.id.myPasswordGo)
    Button mSubmit;
    @BindView(R.id.myPasswordText)
    TextView mPasswordText;
    private Unbinder mUnbinder;

    public dialogFragmentMyInfo() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fragment_my_info, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        clickListener();
        return v;
    }

    private void clickListener() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPasswordText.getText().toString().isEmpty() || mPasswordText.getText().toString().length() != 0) {
                    int password = Integer.parseInt(mPasswordText.getText().toString());
                    if (password == mPassword) {
                        mPasswordContainer.setVisibility(View.GONE);
                        mImageContainer.setVisibility(View.VISIBLE);
                        setImages();
                    } else {
                        Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Enter the password to see the magic", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setImages() {
        mNrsImageProgress.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        mMyImageProgress.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        mNrsImage.setBackgroundColor(Color.parseColor("#000000"));
        mMyImage.setBackgroundColor(Color.parseColor("#000000"));
        Glide.with(getActivity())
                .load(mNrsImageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mNrsImageProgress.setVisibility(View.GONE);
                        setTextBackGround(mNrsImageUrl, mNrsText);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.color.colorPrimaryDark)
                .crossFade()
                .into(mNrsImage);
        Glide.with(getActivity())
                .load(mMyImageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mMyImageProgress.setVisibility(View.GONE);
                        setTextBackGround(mMyImageUrl, mMyText);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.color.colorPrimaryDark)
                .crossFade()
                .into(mMyImage);
    }

    private void setTextBackGround(String url, TextView textView) {
        SetColor color = new SetColor(url, textView);
        color.execute();
    }

    private Palette createPaletteAsync(String url) throws ExecutionException, InterruptedException {
        Bitmap b = Glide.with(getActivity()).load(url).asBitmap().into(100, 100).get();
        return Palette.from(b).generate();
    }

    private void setColor(Palette p, TextView textView) {
        if (p != null) {
            textView.setBackgroundColor(p.getDarkMutedColor(getActivity().getResources().getColor(R.color.colorPrimary)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private class SetColor extends AsyncTask<Void, Void, Palette> {

        String mUrl;
        TextView mTextView;

        SetColor(String u, TextView textView) {
            mUrl = u;
            mTextView = textView;
        }


        @Override
        protected Palette doInBackground(Void... params) {
            try {
                return createPaletteAsync(mUrl);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Palette palette) {
            setColor(palette, mTextView);
            super.onPostExecute(palette);
        }
    }

}

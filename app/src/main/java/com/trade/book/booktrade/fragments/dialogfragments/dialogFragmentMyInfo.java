package com.trade.book.booktrade.fragments.dialogfragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class dialogFragmentMyInfo extends DialogFragment {


    private static final int[] m1 = {116, 104, 101, 71, 111, 111, 100, 71, 117, 121};
    private static final int[] m2 = {78, 101, 119, 97, 110, 116};
    private static final int[] m3 = {78, 111, 111, 98, 105, 101, 32, 78, 111, 111, 98, 101, 114, 115, 111, 110};
    private static String mNrsImageUrl = null;
    private static String mMyImageUrl = null;
    private static String mReyImageUrl = null;
    private static String mMenuImageUrl = null;
    private static int mPassword;
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
    @BindView(R.id.myPasswordLoadingText)
    TextView mLoadingText;
    private Unbinder mUnbinder;
    private int mCounter = 1;


    public dialogFragmentMyInfo() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_fragment_my_info, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mSubmit.setVisibility(View.GONE);
        mPasswordText.setVisibility(View.GONE);
        getValues();
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mSubmit.setTextColor(ContextCompat.getColor(getActivity(), R.color.cardview_dark_background));
        }
        return v;
    }

    private void getValues() {
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlMineQueryAll);
        String url = host + queryFilename;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    setValues(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void setValues(JSONArray array) throws JSONException {
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int id = object.getInt("id");
                String value = object.getString("value");
                set(id, value.replaceAll("\\/", "/"));
                if (i == 2) {
                    setEnabled();
                    clickListener();
                }
            }
        }
    }

    private void setEnabled() {
        if (mLoadingText != null) {
            mLoadingText.setVisibility(View.GONE);
        }
        if (mSubmit != null) {
            mSubmit.setVisibility(View.VISIBLE);
        }
        if (mPasswordText != null) {
            mPasswordText.setVisibility(View.VISIBLE);
        }
    }

    private void set(int id, String v) {
        switch (id) {
            case 1:
                mNrsImageUrl = v;
                break;
            case 2:
                mMyImageUrl = v;
                break;
            case 3:
                mPassword = Integer.parseInt(v);
                break;
            case 4:
                mReyImageUrl = v;
                break;
            case 5:
                mMenuImageUrl = v;
                break;
        }
    }

    private void clickListener() {
        if (mSubmit != null) {
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
                            Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Enter the password to see the magic", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setImages() {
        mNrsImageProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.MULTIPLY);
        mMyImageProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.MULTIPLY);
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
        loadGlideProfileImage(mMyImageUrl, "theGoodGuy");
        setClick();
    }

    private void setClick() {
        if (mMyImage != null) {
            mMyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCounter == 0) {
                        mCounter++;
                        loadGlideProfileImage(mMyImageUrl, mkn(m1));
                    } else if (mCounter == 1) {
                        mCounter++;
                        loadGlideProfileImage(mReyImageUrl, mkn(m2));
                    } else if (mCounter == 2) {
                        mCounter = 0;
                        loadGlideProfileImage(mMenuImageUrl, mkn(m3));
                    }
                }
            });
        }
    }

    private String mkn(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int anArr : arr) {
            sb.append(Character.toString((char) anArr));
        }
        return sb.toString();
    }

    private void loadGlideProfileImage(final String url, final String name) {
        Glide.with(getActivity())
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mMyImageProgress.setVisibility(View.GONE);
                        mMyText.setText(name);
                        setTextBackGround(url, mMyText);
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
            textView.setBackgroundColor(p.getDarkMutedColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary)));
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

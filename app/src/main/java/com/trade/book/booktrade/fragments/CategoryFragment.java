package com.trade.book.booktrade.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.trade.book.booktrade.CategoryViewActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterCategory;
import com.trade.book.booktrade.network.VolleySingleton;
import com.trade.book.booktrade.objects.CategoryObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryFragment extends Fragment {

    private static final String mBaseFileUri = "https://s3-ap-northeast-1.amazonaws.com/shelfbeecategory/";
    @BindView(R.id.cateogoryList)
    ListView catList;
    @BindView(R.id.cateogoryGridList)
    GridView catGridList;
    @BindView(R.id.categoryProgress) ProgressBar mProgressBar;
    private Unbinder mUnbinder;
    private ArrayList<String> mCategoryList;

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cateogory, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initilize();
        buildCategoryListUri();
        return v;
    }

    private void initilize() {
        mProgressBar.setVisibility(View.VISIBLE);
        catList.setVisibility(View.GONE);
        catGridList.setVisibility(View.VISIBLE);
        mCategoryList = new ArrayList<>();
        catGridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryObject object = (CategoryObject) parent.getItemAtPosition(position);
                String s = object.getmName();
                if (s.indexOf(' ') != -1) {
                    s = s.substring(0, s.indexOf(' '));
                }
                String su = buildUri(s);
                Intent cat = new Intent(getActivity(), CategoryViewActivity.class);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuri), su);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuripos), object.getmName());
                startActivity(cat);
            }
        });
    }

    private String buildUri(String val) {
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlSearchCatQuery);
        String url = host + searchFileName;
        String catQuery = "ct";
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(catQuery, val)
                .build()
                .toString();
    }

    private void buildCategoryListUri() {
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlCategoryList);
        String url = host + queryFilename;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    makeCategoryList(response);
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

    private void makeCategoryList(JSONArray array) throws JSONException {
        mProgressBar.setVisibility(View.GONE);
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                mCategoryList.add(object.getString("name"));
            }
            addList();
        }
    }

    private void addList() {
        int start = 97;
        ArrayList<CategoryObject> catObjList = new ArrayList<>();
        for (int i = 0; i < mCategoryList.size(); i++) {
            int num = start + i;
            String url = mBaseFileUri + Character.toString((char) num) + ".jpg";
            catObjList.add(new CategoryObject(mCategoryList.get(i), url));
        }
        adapterCategory adptr = new adapterCategory(getActivity(), catObjList);
        catGridList.setAdapter(adptr);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        }

        if (animation != null) {
            getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    getView().setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return animation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}

package com.trade.book.booktrade.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.AddBook;
import com.trade.book.booktrade.CategoryViewActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterCategory;
import com.trade.book.booktrade.objects.CategoryObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryFragment extends Fragment {

    @BindView(R.id.cateogoryList) ListView catList;
    @BindView(R.id.cateogoryGridList) GridView catGridList;
    private Unbinder mUnbinder;
    private ArrayList<String> mCategoryList;
    private static final String mBaseFileUri = "https://s3-ap-northeast-1.amazonaws.com/shelfbeecategory/";
    private static final int mStartingIndex = 97;

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cateogory, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initilize();
        buildCategoryListUri();
        return v;
    }

    private void initilize() {
        catList.setVisibility(View.GONE);
        catGridList.setVisibility(View.VISIBLE);
        mCategoryList = new ArrayList<>();
        catGridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryObject object = (CategoryObject) parent.getItemAtPosition(position);
                String s = object.getmName();
                if(s.indexOf(' ')!=-1){
                    s = s.substring(0,s.indexOf(' '));
                }
                String su = buildUri(s);
                Intent cat = new Intent(getActivity(),CategoryViewActivity.class);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuri),su);
                //cat.putExtra(getActivity().getResources().getString(R.string.intencateuripos),position);
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuripos),object.getmName());
                startActivity(cat);
            }
        });
    }

    private String buildUri(String val){
        String host = getResources().getString(R.string.urlServer);
        String searchFileName = getResources().getString(R.string.urlSearchCatQuery);
        String url = host + searchFileName;
        String catQuery = "ct";
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(catQuery, val)
                .build()
                .toString();
    }

    private void buildCategoryListUri(){
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlCategoryList);
        String url = host+queryFilename;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void makeCategoryList(JSONArray array) throws JSONException {
        if(array.length()>0){
            for(int i=0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                mCategoryList.add(object.getString("name"));
            }
            addList();
        }
    }

    private void addList(){
        int start = 97;
        ArrayList<CategoryObject> catObjList = new ArrayList<>();
        for(int i=0;i<mCategoryList.size();i++){
            int num = start+i;
            String url = mBaseFileUri+Character.toString ((char) num)+".jpg";
            catObjList.add(new CategoryObject(mCategoryList.get(i),url));
        }

        adapterCategory adptr = new adapterCategory(getActivity(),catObjList);
        catGridList.setAdapter(adptr);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}

package com.trade.book.booktrade.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import com.trade.book.booktrade.CategoryViewActivity;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.adapters.adapterCategory;
import com.trade.book.booktrade.objects.CategoryObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment {

    @BindView(R.id.cateogoryList) ListView catList;
    @BindView(R.id.cateogoryGridList) GridView catGridList;

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cateogory, container, false);
        ButterKnife.bind(this,v);
        initilize();
        addList();
        return v;
    }

    private void initilize() {
        catList.setVisibility(View.GONE);
        catGridList.setVisibility(View.VISIBLE);
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
                cat.putExtra(getActivity().getResources().getString(R.string.intencateuripos),position);
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

    private void addList(){
        String[] arr = getResources().getStringArray(R.array.bookCateogories);
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(arr));
        ArrayList<CategoryObject> catObjList = new ArrayList<>();
        catObjList.add(new CategoryObject (stringList.get(0),getResources().getDrawable(R.drawable.a)));
        catObjList.add(new CategoryObject (stringList.get(1),getResources().getDrawable(R.drawable.b)));
        catObjList.add(new CategoryObject (stringList.get(2),getResources().getDrawable(R.drawable.c)));
        catObjList.add(new CategoryObject (stringList.get(3),getResources().getDrawable(R.drawable.d)));
        catObjList.add(new CategoryObject (stringList.get(4),getResources().getDrawable(R.drawable.e)));
        catObjList.add(new CategoryObject (stringList.get(5),getResources().getDrawable(R.drawable.f)));
        catObjList.add(new CategoryObject (stringList.get(6),getResources().getDrawable(R.drawable.g)));
        catObjList.add(new CategoryObject (stringList.get(7),getResources().getDrawable(R.drawable.h)));
        catObjList.add(new CategoryObject (stringList.get(8),getResources().getDrawable(R.drawable.i)));
        catObjList.add(new CategoryObject (stringList.get(9),getResources().getDrawable(R.drawable.j)));
        catObjList.add(new CategoryObject (stringList.get(10),getResources().getDrawable(R.drawable.k)));
        catObjList.add(new CategoryObject (stringList.get(11),getResources().getDrawable(R.drawable.l)));
        catObjList.add(new CategoryObject (stringList.get(12),getResources().getDrawable(R.drawable.m)));
        catObjList.add(new CategoryObject (stringList.get(13),getResources().getDrawable(R.drawable.n)));
        catObjList.add(new CategoryObject (stringList.get(14),getResources().getDrawable(R.drawable.o)));
        catObjList.add(new CategoryObject (stringList.get(15),getResources().getDrawable(R.drawable.p)));
        catObjList.add(new CategoryObject (stringList.get(16),getResources().getDrawable(R.drawable.q)));
        catObjList.add(new CategoryObject (stringList.get(17),getResources().getDrawable(R.drawable.r)));
        catObjList.add(new CategoryObject (stringList.get(18),getResources().getDrawable(R.drawable.s)));
        adapterCategory adptr = new adapterCategory(getActivity(),catObjList);
        catGridList.setAdapter(adptr);
    }

}

package com.trade.book.booktrade.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.trade.book.booktrade.adapters.adapterBookList;
import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.PurchaseActivity;
import com.trade.book.booktrade.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class fragmentBookList extends Fragment {


    GridView bookListGrid;
    ArrayList<BookObject> bookList = new ArrayList<>();

    public fragmentBookList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_book_list,container,false);
        initilize(v);
        getList();
        return v;
    }

    private void getList(){
        String host = getResources().getString(R.string.urlServer);
        String queryFilename = getResources().getString(R.string.urlQueryAvailable);
        String url = host+queryFilename;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    addToList(response);
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

    private void addToList(JSONArray response) throws JSONException {
        for(int i =0;i<response.length();i++){
            JSONObject jsonObject = response.getJSONObject(i);
            int bid = jsonObject.getInt("id");
            String name = jsonObject.getString("Name");
            String publisher = jsonObject.getString("Publisher");
            int costPrice = jsonObject.getInt("CostPrice");
            int sellingPrice = jsonObject.getInt("SellingPrice");
            int edition = jsonObject.getInt("Edition");
            String description = jsonObject.getString("Description");
            String condtn = jsonObject.getString("Cndtn");
            String cateogory = jsonObject.getString("Cateogory");
            String userId = jsonObject.getString("userId");
            int itemId = jsonObject.getInt("id");
            String photoUrlName0 = jsonObject.getString("pic0");
            String photoUrlName1 = jsonObject.getString("pic1");
            String photoUrlName2 = jsonObject.getString("pic2");
            String photoUrlName3 = jsonObject.getString("pic3");
            String photoUrlName4 = jsonObject.getString("pic4");
            String photoUrlName5 = jsonObject.getString("pic5");
            String photoUrlName6 = jsonObject.getString("pic6");
            String photoUrlName7 = jsonObject.getString("pic7");
            bookList.add(new BookObject(bid,name,publisher,costPrice,sellingPrice,edition,description,condtn,cateogory,userId,itemId
                    ,photoUrlName0,photoUrlName1,photoUrlName2,photoUrlName3,photoUrlName4,photoUrlName5,photoUrlName6,photoUrlName7));
        }
        adapterBookList bookAdapter = new adapterBookList(getActivity(),bookList);
        bookListGrid.setAdapter(bookAdapter);
    }

    private void initilize(View v) {
        bookListGrid = (GridView)v.findViewById(R.id.bookListGrid);
        bookListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookObject bookObject = (BookObject) parent.getItemAtPosition(position);
                Intent detail  = new Intent(getActivity(),PurchaseActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(getActivity().getResources().getString(R.string.intenKeyObejct), bookObject);
                detail.putExtras(b);
                startActivity(detail);
            }
        });
    }

}

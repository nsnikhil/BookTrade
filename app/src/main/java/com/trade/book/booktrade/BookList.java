package com.trade.book.booktrade;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class BookList extends Fragment {


    GridView bookListGrid;
    ArrayList<BookObject> bookList = new ArrayList<>();

    public BookList() {
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
        String queryFilename = getResources().getString(R.string.urlQuery);
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
            String name = jsonObject.getString("Name");
            String publisher = jsonObject.getString("Publisher");
            int costPrice = jsonObject.getInt("CostPrice");
            int sellingPrice = jsonObject.getInt("SellingPrice");
            int edition = jsonObject.getInt("Edition");
            String description = jsonObject.getString("Description");
            String condtn = jsonObject.getString("Cndtn");
            String cateogory = jsonObject.getString("Cateogory");
            int userId = jsonObject.getInt("userId");
            int itemId = jsonObject.getInt("id");
            bookList.add(new BookObject(name,publisher,costPrice,sellingPrice,edition,description,condtn,cateogory,userId,itemId));
        }
        BookListAdapter bookAdapter = new BookListAdapter(getActivity(),bookList);
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

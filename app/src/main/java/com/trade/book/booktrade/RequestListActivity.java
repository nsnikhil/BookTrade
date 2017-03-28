package com.trade.book.booktrade;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import com.trade.book.booktrade.adapters.*;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentRequest;
import com.trade.book.booktrade.objects.BookObject;
import com.trade.book.booktrade.objects.RequestObject;

public class RequestListActivity extends AppCompatActivity {

    ListView requestList;
    Toolbar requestToolbar;
    FloatingActionButton requestAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        intilize();
        addList();
    }

    private void intilize() {
        requestAdd = (FloatingActionButton) findViewById(R.id.requestAdd);
        requestToolbar = (Toolbar) findViewById(R.id.toolbarRequest);
        setSupportActionBar(requestToolbar);
        getSupportActionBar().setTitle("Book Request");
        requestList = (ListView) findViewById(R.id.requestList);
        requestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentRequest dialogFragmentRequest = new dialogFragmentRequest();
                dialogFragmentRequest.show(getSupportFragmentManager(), "request");
            }
        });
    }

    private void addList() {
        ArrayList<RequestObject> name = new ArrayList<>();
        name.add(new RequestObject("Physics","Publishera"));
        name.add(new RequestObject("Chemistry","Publisherb"));
        name.add(new RequestObject("Biology","Publisherc"));
        name.add(new RequestObject("Maths","Publisherd"));
        name.add(new RequestObject("History","Publishere"));
        name.add(new RequestObject("Geogrpahy","Publisherf"));
        name.add(new RequestObject("Accounts","Publisherg"));
        name.add(new RequestObject("Economics","Publisherh"));
        name.add(new RequestObject("Thermodynamics","Publisheri"));
        adapterRequest adapter = new adapterRequest(getApplicationContext(), name);
        requestList.setAdapter(adapter);
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder have = new AlertDialog.Builder(RequestListActivity.this)
                        .setMessage("Do you want to sell this book")
                        .setPositiveButton("Sell", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Will start add books activity",Toast.LENGTH_SHORT).show();
                            }
                        });
                have.create().show();
            }
        });
    }
}

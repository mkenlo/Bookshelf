package com.mkenlo.bookshelf;


import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {

    EditText searchText;
    ImageButton searchButton;
    String keyword;
    RecyclerView recyclerView;
    JSONArray jsonBookList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        jsonBookList = new JSONArray();
        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), searchText.getText().toString(), Toast.LENGTH_SHORT).show();
                try {
                    searchKeyword(searchText.getText().toString());
                    updateIU();
                }catch(JSONException ex){
                    ex.printStackTrace();
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.book_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        updateIU();
    }

    public void searchKeyword(String keyword) throws JSONException {
        FetchBookTask fetchBookTask = new FetchBookTask(getBaseContext());
        fetchBookTask.execute(keyword);
        jsonBookList = fetchBookTask.getResult();

    }

    public void updateIU(){
        BookRecyclerAdapter mAdapter = new BookRecyclerAdapter(getBaseContext(), jsonBookList);
        recyclerView.setAdapter(mAdapter);
    }






}

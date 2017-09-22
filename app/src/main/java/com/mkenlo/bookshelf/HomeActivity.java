package com.mkenlo.bookshelf;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class HomeActivity extends AppCompatActivity {

    EditText searchText;
    ImageButton searchButton;
    RecyclerView recyclerView;
    JSONArray jsonBookList;
    TextView instructionText;
    BookRecyclerAdapter mAdapter;
    static final String APP_ID = "AIzaSyB5N0X145i0P6EozLBJF9Z0CCD4xlP_wW8";
    static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instructionText = (TextView) findViewById(R.id.instructions_text);
        if (savedInstanceState != null) {
            restoreStateBookList(savedInstanceState);
        } else
            jsonBookList = new JSONArray();
        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        recyclerView = (RecyclerView) findViewById(R.id.book_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new BookRecyclerAdapter(getBaseContext(), jsonBookList);
        recyclerView.setAdapter(mAdapter);

        if (!isDeviceConnectedToInternet()) {
            showNotif("Can't reach Network");
        }
    }

    public void searchKeyword(View v) throws JSONException {
        if (isDeviceConnectedToInternet()) {
            String keyword = searchText.getText().toString();
            FetchBookTask fetchBookTask = new FetchBookTask(getBaseContext());
            fetchBookTask.execute(keyword);

            if (jsonBookList != null) {
                updateIU();
            } else {
                showNotif("NO BOOKS FOUND");
            }
        } else {
            showNotif("Can't reach Network");
        }

    }

    public void updateIU() {
        BookRecyclerAdapter mAdapter = new BookRecyclerAdapter(getBaseContext(), jsonBookList);
        recyclerView.setAdapter(mAdapter);
    }

    public void showNotif(String msg) {
        Snackbar snackbar = Snackbar.make(searchButton, msg, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    public boolean isDeviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) ? true : false;
    }

    public void restoreStateBookList(Bundle savedInstanceState) {
        try {
            jsonBookList = new JSONArray(savedInstanceState.getString("bookList"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("bookList", jsonBookList.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreStateBookList(savedInstanceState);
    }

    public class FetchBookTask extends AsyncTask<String, Void, JSONArray> {

        Context context;

        public FetchBookTask(Context context) {
            this.context = context;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            try {
                String jsonStr = getBookListData(params[0]);
                JSONObject jsonObject = new JSONObject(jsonStr);
                return jsonObject.getJSONArray("items");

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            jsonBookList = result;
        }

        private String getBookListData(String keyword) {

            String jsonResponse = "";
            HttpURLConnection request = null;
            try {
                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendQueryParameter("q", keyword)
                        .appendQueryParameter("key", APP_ID)
                        .build();

                URL url = new URL(builtUri.toString());
                request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(request.getInputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                StringBuilder buffer = new StringBuilder();
                while (line != null) {
                    buffer.append(line);
                    line = reader.readLine();
                }
                in.close();
                jsonResponse = buffer.toString();
            } catch (IOException ex) {
                Log.e("I/O EXCEPTION", ex.getMessage());
            } finally {
                if (request != null)
                    request.disconnect();
            }
            return jsonResponse;

        }

    }

}

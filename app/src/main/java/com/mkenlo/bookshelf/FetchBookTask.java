package com.mkenlo.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by Melanie on 9/13/2017.
 */

public class FetchBookTask extends AsyncTask<String, Void, JSONArray> {

    Context context;
    JSONArray result = null;

    public FetchBookTask(Context context) {
        this.context = context;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        try {
            String jsonStr =  getBookListData(params[0]);
            JSONObject jsonObject = new JSONObject(jsonStr);
            return jsonObject.getJSONArray("items");

        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        this.result = result;
    }

    private String getBookListData(String keyword){

        final String baseUrl = "https://www.googleapis.com/books/v1/volumes";
        final String APPID = "AIzaSyB5N0X145i0P6EozLBJF9Z0CCD4xlP_wW8";

        String jsonResponse="";
        HttpURLConnection request = null;
        try {
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("q", keyword)
                    .appendQueryParameter("key",APPID)
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
        }catch (IOException ex){
            Log.e("I/O EXCEPTION", ex.getMessage());
        }
        finally {
            if(request!=null)
                request.disconnect();
        }
        return jsonResponse;

    }

    public JSONArray getResult() {
        return result;
    }
}

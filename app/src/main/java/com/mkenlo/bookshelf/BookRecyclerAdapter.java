package com.mkenlo.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Melanie on 9/15/2017.
 */

public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {

    Context context;
    JSONArray mBookList;

    public BookRecyclerAdapter(Context context, JSONArray jsonBookList) {
        this.context = context;
        this.mBookList = jsonBookList;
    }

    @Override
    public BookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new BookRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BookRecyclerAdapter.ViewHolder holder, int position) {

        try {
            JSONObject book = mBookList.getJSONObject(position);
            holder.book_title.setText(book.getString("title"));
            holder.book_author.setText(book.getJSONArray("authors").getString(0));
            DownloadImageTask task = new DownloadImageTask(holder.book_thumbnail);
            task.execute(book.getJSONObject("imageLinks").getString("smallThumbnail"));

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mBookList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView book_thumbnail;
        TextView book_title;
        TextView book_author;

        public ViewHolder(View v) {
            super(v);
            book_author = (TextView) v.findViewById(R.id.book_author);
            book_title = (TextView) v.findViewById(R.id.book_title);
            book_thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap thumbnail = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                thumbnail = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return thumbnail;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

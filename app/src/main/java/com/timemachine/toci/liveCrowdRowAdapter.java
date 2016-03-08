package com.timemachine.toci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Victor Ruelas on 3/7/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class liveCrowdRowAdapter extends ArrayAdapter<liveCrowdRow> {

    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/";

    Context context;
    int layoutResourceId;
    liveCrowdRow crowds[] = null;

    public liveCrowdRowAdapter(Context context, int layoutResourceId, liveCrowdRow[] crowds) {
        super(context, layoutResourceId, crowds);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.crowds = crowds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final liveCrowdRowHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new liveCrowdRowHolder();
            holder.livepic = (ImageView) row.findViewById(R.id.livepic);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.subtitle = (TextView) row.findViewById(R.id.subtitle);
            holder.distance = (TextView) row.findViewById(R.id.distance);

            row.setTag(holder);
        }
        else {
            holder = (liveCrowdRowHolder) row.getTag();
        }

        final liveCrowdRow crowdRow = crowds[position];
        //new HttpAsyncTask().execute(sortScript);
        holder.title.setText(crowdRow.title);
        holder.subtitle.setText(crowdRow.subtitle);
        holder.distance.setText(crowdRow.distance);
//        new DisplayImageFromURL(holder.livepic).execute(crowdRow.picUrl);

        //Picasso.with(context).load(crowdRow.picUrl).into(holder.livepic);

        new HttpAsyncTask() {
            @Override
                    public void onPostExecute(String picName) {
                        Picasso.with(context).load(imageBaseDirectory+picName).into(holder.livepic);
            }
        }.execute(crowdRow.picUrl);

        return row;
    }

    static class liveCrowdRowHolder {

        ImageView livepic;
        TextView title;
        TextView subtitle;
        TextView distance;


    }

        /*
     * Helper function to convert php stream result to string
     */

    public static String GET(String url) {
        InputStream inputStream;
        String result = "";

        try {
            // create HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputStream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "GET method to get php response was not successful";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputStream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";

        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        return result;
    }

    /**
     * Task {@link AsyncTask} to fetch file name of the latest picture.
     * Returns String of file name.
     */

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {


        HttpAsyncTask() {

        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        // constructor
        public DisplayImageFromURL(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Victor Ruelas on 3/17/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class GetFavCrowds extends AsyncTask<String, Void, LiveCrowd[]> {

    public interface AsyncResponse {
        void onAsyncTaskFinish(LiveCrowd[] crowds);
    }

    private final static String TAG = GetFavCrowds.class.getSimpleName();

    public AsyncResponse delegate = null;

    // Helper fields to help store favorite settings
    private Context mContext;
    AppPrefs mAppPrefs;


    public GetFavCrowds(Context context, AsyncResponse delegate) {
        this.mContext = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        mAppPrefs = new AppPrefs(mContext);
    }

    @Override
    protected LiveCrowd[] doInBackground (String...params){

        LiveCrowd[] crowds = SerializeLiveCrowd.fromJson(new ArrayList<>(mAppPrefs.getFav_crowds()));

        try {

            for (int i = 0; i < crowds.length; i++) {
                String crowdId = crowds[i].getId();
                String crowdCity = crowds[i].getCity();
                HashMap<Integer, ArrayList<String>> picUrls = getPicUrls(crowdCity, crowdId);
                String timeAgo = picUrls.get(picUrls.size()-1).get(1);
                crowds[i].setPicUrls(picUrls);
                crowds[i].setTimeago(timeAgo);
            }

            return crowds;

        } catch (Exception e) {
            Log.d(TAG, "Error getting favorite crowds pictures");
            e.printStackTrace();
        }

        return crowds;
    }

    @Override
    protected void onPostExecute (LiveCrowd[] crowds) {
        super.onPostExecute(crowds);

        delegate.onAsyncTaskFinish(crowds);
//        Log.d(TAG, crowds.toString());
    }

    public static HashMap<Integer, ArrayList<String>> getPicUrls(String crowdCity, String crowdId) {

        String response;

        if (crowdId != null) {

            try {
                String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowdpics.php?" +
                        "city=" + URLEncoder.encode(crowdCity) +
                        "&id=" + URLEncoder.encode(crowdId) + "";
                URI urlObj = new URI(link);
                HttpClient thisApp = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(urlObj);
                HttpResponse httpResponse = thisApp.execute(request);
                HttpEntity serverObj = httpResponse.getEntity();
                response = EntityUtils.toString(serverObj);
                Log.d(TAG, "getPicUrls response: " + response);

                final JSONArray jsonArray = new JSONArray(response);

                /**
                 * Method to store picture urls and timestamps
                 */
                HashMap<Integer, ArrayList<String>> picUrls = new HashMap<>();
                picUrls.put(0, new ArrayList<String>() {{
                    add("http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/placeholders/add_picture_place_holder_xxxhdpi.png");
                    add("");
                }});

                if (jsonArray != null && jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final String url = jsonArray.getString(i);
                        final String timeAgo = getTimeAgo(url);
                        picUrls.put(i, new ArrayList<String>() {{
                            add(url);
                            add(timeAgo);
                        }});
                    }
                }

                return picUrls;

            } catch (JSONException e) {
                Log.d(TAG, "could not get id from JSONArray: ");
                e.printStackTrace();
            } catch (URISyntaxException e) {
                Log.d(TAG, "error syntax with URI");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "error with HttpResponse");
                e.printStackTrace();
            }
        }
        return null;
        /** finish adding picture urls */
    }

    public static String getTimeAgo(String url) {

        try {
            // get timestamp from url
            String timeStamp = getTimeStamp(url);
            // get time ago from timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(timeStamp);
            long epoch = date.getTime();
            CharSequence timePassed = DateUtils.getRelativeTimeSpanString (epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

            // get actual time
            Date date1 = new SimpleDateFormat("HH:mm:ss").parse(timeStamp.substring(10));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("h:mm a");

            return (timePassed.toString() + ", " + dateFormat1.format(date1));

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getTimeStamp(String url) throws java.text.ParseException {

        // date format: yyyy-MM-dd HH:mm:ss

        int beginIndex = url.length() - 19;
        int endIndex = url.length() - 4;

        StringBuffer extractedTimeStamp = new StringBuffer(url.substring(beginIndex, endIndex).replace('_', ' '));
        extractedTimeStamp.insert(4,"-")
                .insert(7, "-")
                .insert(13, ":")
                .insert(16, ":");

        return extractedTimeStamp.toString();
    }
}

package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
public class GetCrowds extends AsyncTask<String, Void, LiveCrowd[]> {

    private final static String TAG = GetCrowds.class.getSimpleName();

    public interface AsyncResponse {
        void onAsyncTaskFinish(LiveCrowd[] crowds);
    }

    public AsyncResponse delegate = null;

    public GetCrowds(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected LiveCrowd[] doInBackground (String...params){

        JSONArray result;

        String filter = params[0];

        String query = params[1];

        result = getList(filter, query);

        LiveCrowd[] crowds = null;

        if (result != null) {

            crowds = new LiveCrowd[result.length()];
            try {

                for (int i = 0; i < result.length(); i++) {
                    String crowdId = result.getJSONObject(i).getString("id");
                    String crowdName = result.getJSONObject(i).getString("name");
                    String crowdCity = result.getJSONObject(i).getString("city");
                    HashMap<Integer, ArrayList<String>> picUrls = getPicUrls(crowdCity, crowdId);
                    String timeAgo = picUrls.get(picUrls.size()-1).get(1);
                    String distance = "";

                    crowds[i] = new LiveCrowd(crowdId, crowdName,
                            crowdCity, timeAgo, distance, picUrls);
                }

                return crowds;

            } catch (JSONException e) {
                Log.d(TAG, "error getting id and name from server response");
                e.printStackTrace();
            }
        }

        return crowds;

    }

    @Override
    protected void onPostExecute (LiveCrowd[] crowds) {
        super.onPostExecute(crowds);

        delegate.onAsyncTaskFinish(crowds);

    }


    /**
     * Helper method for getting list of crowds in a city
     */
    private JSONArray getList(String filter, String query) {

        String response;

        if (filter.equals("all")) {
            try {
                String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchcrowds.php?city=" + URLEncoder.encode(query) + "";
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                Log.d(TAG, "GetCrowds.getList response type: " + filter + ", and query response: " + response);

                return new JSONArray(response);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (filter.equals("favorites")) {
            try {
                String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchcrowdsbyid.php?ids=" + query + "";
                Log.d(TAG + " url: ", link);
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                Log.d(TAG, "GetCrowds.getList response type: " + filter + ", and query response: " + response);

                return new JSONArray(response);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // If filter is invalid return null
        return null;
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
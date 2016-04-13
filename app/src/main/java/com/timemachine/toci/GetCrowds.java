//
//package com.timemachine.toci;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URLEncoder;
//
//
///**
// * Created by Victor Ruelas on 3/17/16.
// * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
// */
//public class GetCrowds  extends AsyncTask<String, Void, liveCrowdRow[]> {
//
//    private final static String TAG = GetCrowds.class.getSimpleName();
//
//    public interface AsyncResponse {
//        void onAsyncTaskFinish(liveCrowdRow[] crowds);
//    }
//
//    Context context;
//
//    public AsyncResponse delegate = null;
//
//    public GetCrowds(AsyncResponse delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected liveCrowdRow[] doInBackground (String...params){
//
//        JSONArray result;
//
//        String fromCity = params[0];
//
//        result = getList(fromCity);
//
//        liveCrowdRow[] crowds = null;
//
//        if (result != null) {
//
//            crowds = new liveCrowdRow[result.length()];
//            try {
//
//                for (int i = 0; i < result.length(); i++) {
//                    String crowdId = result.getJSONObject(i).getString("id");
//                    String crowdName = result.getJSONObject(i).getString("name");
//                    String timeStamp = "6 minutes ago, 10:24 PM";
//                    String distance = "";
//                    String[] picUrls = getPicUrl(fromCity, crowdId);
//
//                    crowds[i] = new liveCrowdRow(crowdId, crowdName,
//                            fromCity, timeStamp, distance, picUrls,
//                            LivePicsGalleryActivity.class);
//                }
//
//                return crowds;
//
//            } catch (JSONException e) {
//                Log.d(TAG, "error getting id and name from server response");
//                e.printStackTrace();
//            }
//        }
//
//        return crowds;
//
//    }
//
//    @Override
//    protected void onPostExecute (liveCrowdRow[] crowds) {
//        super.onPostExecute(crowds);
//
//        delegate.onAsyncTaskFinish(crowds);
//
//    }
//
//
//    /**
//     * Helper method for getting list of crowds in a city
//     */
//    private JSONArray getList(String city) {
//
//        String response;
//
//        try {
//            String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchcrowds.php?city=" + URLEncoder.encode(city) + "";
//            URI url = new URI(link);
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet();
//            request.setURI(url);
//            HttpResponse httpResponse = client.execute(request);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            response = EntityUtils.toString(httpEntity);
//            Log.d(TAG, "response is: " + response);
//            return new JSONArray(response);
//
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private String[] getPicUrl(String crowdCity, String crowdId) {
//
//        String response;
//
//        if (crowdId != null) {
//
//            try {
//                String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowdpics.php?" +
//                        "city=" + URLEncoder.encode(crowdCity) +
//                        "&id=" + URLEncoder.encode(crowdId) + "";
//                URI urlObj = new URI(link);
//                HttpClient thisApp = new DefaultHttpClient();
//                HttpGet request = new HttpGet();
//                request.setURI(urlObj);
//                HttpResponse httpResponse = thisApp.execute(request);
//                HttpEntity serverObj = httpResponse.getEntity();
//                response = EntityUtils.toString(serverObj);
//                Log.d(TAG, "getPicUrl response: " + response);
//
//                JSONArray jsonArray = new JSONArray(response);
//                String[] livePicsUrls = {"http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/placeholders/add_picture_place_holder_xxxhdpi.png"};
//
//                if (jsonArray != null && jsonArray.length() != 0 ) {
//                    /**
//                     * add map of picture urls
//                     */
//                    livePicsUrls = new String[jsonArray.length()];
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                            livePicsUrls[i] = jsonArray.getString(i);
//                        }
//
//                }
//
//                return livePicsUrls;
//
//                /** finish adding picture urls */
//
//            } catch (JSONException e) {
//                Log.d(TAG, "could not get id from JSONArray: ");
//                e.printStackTrace();
//            } catch (URISyntaxException e) {
//                Log.d(TAG, "error syntax with URI");
//                e.printStackTrace();
//            } catch (IOException e) {
//                Log.d(TAG, "error with HttpResponse");
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//}

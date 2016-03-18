package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor Ruelas on 3/17/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class GetCrowds  extends AsyncTask<String, Void, JSONArray> {

    public interface AsyncResponse {
        void onAsyncTaskFinish(ArrayList<HashMap<String, String>> output);
    }

    Context context;

    public AsyncResponse delegate = null;

    public GetCrowds(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected JSONArray doInBackground (String...params){

        String response;

        try {

            String paramCity = params[0];
            String link = "http://crowdzeeker.com/AppCrowdZeeker/fetchcrowds.php?city=" + URLEncoder.encode(paramCity) + "";
            URI url = new URI(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(url);
            HttpResponse httpResponse = client.execute(request);
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            Log.d("response is", response);

            return new JSONArray(response);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute (JSONArray result){
        super.onPostExecute(result);

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        if (result != null) {

            try {
                for (int x = 0; x < result.length(); x++) {

                    HashMap<String, String> map = new HashMap<>();

                    map.put("id", result.getJSONObject(x).getString("id"));

                    map.put("name", result.getJSONObject(x).getString("name"));

                    list.add(map);

                    delegate.onAsyncTaskFinish(list);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(context, "Network Problem", Toast.LENGTH_LONG).show();
        }
    }

}

package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Ruelas on 5/11/17.
 */

public class RemoveCrowdFromDatabase extends AsyncTask<String, Void, String> {

    Context context;

    @Override
    protected String doInBackground(String... params) {
        String paramPlaceId = params[0].replace("'","''");
        String paramPlaceCity = params[1].replace("'","''");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", paramPlaceId));
        nameValuePairs.add(new BasicNameValuePair("city", paramPlaceCity));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Config.REMOVE_CROWD_TO_DATABASE_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

        } catch (ClientProtocolException e) {
            return "Failed to remove: ClientProtocolException.";
        } catch (IOException e) {
            return "Failed to remove: IOException";
        }

        return "Successfully removed crowd.";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}

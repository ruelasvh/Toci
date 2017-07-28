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
 * Created by Victor Ruelas on 3/17/16.
 */
public class AddCrowdToDatabase extends AsyncTask<String, Void, String> {

    private static final String TAG = AddCrowdToDatabase.class.getSimpleName();

    Context context;

    @Override
    protected String doInBackground(String... params) {
        String paramPlaceId = params[0].replace("'","''");
        String paramPlaceName = params[1].replace("'", "''");
        String paramPlaceAddress = params[2].replace("'", "''");
        String paramLatLng = params[3].replace("'", "''");
        String paramPlaceCity = params[4].replace("'","''");
        Log.d(TAG, "parameters to insert to database: " + paramPlaceId
                + ", " + paramPlaceName + ", " + paramPlaceCity);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", paramPlaceId));
        nameValuePairs.add(new BasicNameValuePair("name", paramPlaceName));
        nameValuePairs.add(new BasicNameValuePair("address", paramPlaceAddress));
        nameValuePairs.add(new BasicNameValuePair("latlng", paramLatLng));
        nameValuePairs.add(new BasicNameValuePair("city", paramPlaceCity));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Config.ADD_CROWD_TO_DATABASE_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            Log.d(TAG, entity.toString());

        } catch (ClientProtocolException e) {
            return "Failed to upload: ClientProtocolException.";
        } catch (IOException e) {
            return "Failed to upload: IOException";
        }

        return "Successfully added new LiveCrowd.";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}

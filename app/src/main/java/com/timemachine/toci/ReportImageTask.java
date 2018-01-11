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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Ruelas on 10/24/17.
 */
public class ReportImageTask extends AsyncTask<String, Void, String> {

    Context context;

    @Override
    protected String doInBackground(String... params) {
        String paramImageId = params[0];
        String paramImageUrl = params[1];

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", paramImageId));
        nameValuePairs.add(new BasicNameValuePair("image_url", paramImageUrl));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Config.REPORT_IMAGE_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            String responseMessage = EntityUtils.toString(response.getEntity());
            return responseMessage;

        } catch (ClientProtocolException e) {
            return "Failed to report image: ClientProtocolException.";
        } catch (IOException e) {
            return "Failed to report image: IOException";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Use result from instance of this class
    }
}

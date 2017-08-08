package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Victor Ruelas on 8/6/17.
 */

public class GetCities extends AsyncTask<Void, Void, GetCities.Result> {

    Context mContext;

    public GetCities(Context context) {
        mContext = context;
    }

    static class Result {
        public List<String> mResultValue;
        public Exception mException;
        public Result(List<String> resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
    }

    @Override
    protected void onPreExecute() {
        Network network = new Network(mContext);
        if (!network.isOnline()) {
            cancel(true);
        }
    }

    @Override
    protected Result doInBackground(Void...arg0) {
        Result result = null;

        if (!isCancelled()) {
            try {
                String link = Config.FETCH_ALL_CITIES_URL;
                URL url = new URL(link);
                List<String> allCities = fetchCitiesList(url);
                if (allCities != null) {
                    result = new Result(allCities);
                } else {
                    throw new IOException("No response received.");
                }
            } catch (IOException e) {
                result = new Result(e);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && result.mResultValue != null) {
            // The result is available
        }
    }

    private List<String> fetchCitiesList(URL url) {
        JSONArray jsonArray;
        HttpURLConnection connection;
        List<String> result = new ArrayList<>();

        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timed out for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, HTTP is GET.
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            StringBuffer response = new StringBuffer();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
            } else {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as a JSONArray
            jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                result.add(obj.getString("city") + ", " + obj.getString("state"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

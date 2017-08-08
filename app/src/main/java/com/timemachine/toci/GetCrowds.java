package com.timemachine.toci;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Victor Ruelas on 3/17/16.
 */
public class GetCrowds extends AsyncTask<String, Void, LiveCrowd[]> implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String BY_CITY = "BY_CITY";

    private final static String BY_ID = "BY_ID";

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private Location currentLocation;

    private Context context;

    public interface AsyncResponse {
        void onAsyncTaskFinish(LiveCrowd[] crowds);
    }

    public AsyncResponse delegate = null;

    public GetCrowds(Context context, AsyncResponse delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        // Create instance of GoogleApiClient
        buildGoogleLocationApi(context);
    }

    @Override
    protected LiveCrowd[] doInBackground (String...params){
        // Connect to Location API
        mGoogleApiClient.connect();

        JSONArray result;

        String filter = params[0];

        String query = params[1];

        result = getList(filter, query);

        LiveCrowd[] crowds = null;

        if (result != null ) {

            crowds = new LiveCrowd[result.length()];
            try {

                for (int i = 0; i < result.length(); i++) {
                    String crowdId = result.getJSONObject(i).getString("id");
                    String crowdName = result.getJSONObject(i).getString("name");
                    String crowdAddress = result.getJSONObject(i).getString("address");
                    String crowdLatLng = result.getJSONObject(i).getString("latlng");
                    String crowdCity = result.getJSONObject(i).getString("city");
                    HashMap<Integer, ArrayList<String>> picUrls = getPicUrls(crowdCity, crowdId);
                    String timeAgo = picUrls.get(picUrls.size()-1).get(1);
                    // Create instance of location for crowd
                    Location crowdLocation = new Location("");
                    String[] latlng = crowdLatLng.split(",");
                    double latitude = Double.parseDouble(latlng[0]);
                    double longitude = Double.parseDouble(latlng[1]);
                    crowdLocation.setLatitude(latitude);
                    crowdLocation.setLongitude(longitude);
                    float distanceTo = round(mLastLocation.distanceTo(crowdLocation)/((float)1609.34),2);

                    crowds[i] = new LiveCrowd(crowdId, crowdName, crowdAddress,
                            crowdLatLng, crowdCity, timeAgo, distanceTo, picUrls);

                    // Escape early if cancel() is called
                    if (isCancelled()) break;
                }

                sortCrowdsByClosest(crowds);

                return crowds;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return crowds;

    }

    @Override
    protected void onPostExecute (LiveCrowd[] crowds) {
        super.onPostExecute(crowds);
        // Send crowds to interface
        delegate.onAsyncTaskFinish(crowds);
        // Disconnect from Location API
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if ( ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    /** Helper method to sort crowds by closest to user **/
    public void sortCrowdsByClosest(LiveCrowd[] crowds) {
        try {
            int listLength = crowds.length;
            for (int i = 1; i < listLength; i++) {
                float distance = crowds[i].getDistance();
                LiveCrowd tempCrowd = crowds[i];
                int j = i - 1;
                while ( j >= 0 && crowds[j].getDistance() > distance) {
                    crowds[j+1] = crowds[j];
                    j = j - 1;
                }
                crowds[j+1] = tempCrowd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    /**
     * Helper method for getting list of crowds in a city
     */
    private JSONArray getList(String filter, String query) {

        String response;

        if (filter.equals(BY_CITY)) {
            try {
                String link = Config.FETCH_CROWDS_BY_CITY_URL + "?city=" + URLEncoder.encode(query, "UTF-8") + "";
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);

                return new JSONArray(response);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (filter.equals(BY_ID)) {
            try {
                String link = Config.FETCH_CROWDS_BY_ID_URL + "?ids=" + URLEncoder.encode(query, "UTF-8") + "";
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);

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
                String link = Config.FETCH_LATEST_CROWD_PICS_URL + "?" +
                        "id=" + URLEncoder.encode(crowdId, "UTF-8");
                URI urlObj = new URI(link);
                HttpClient thisApp = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(urlObj);
                HttpResponse httpResponse = thisApp.execute(request);
                HttpEntity serverObj = httpResponse.getEntity();
                response = EntityUtils.toString(serverObj);

                final JSONArray jsonArray = new JSONArray(response);

                /**
                 * Method to store picture urls and timestamps
                 */
                HashMap<Integer, ArrayList<String>> picUrls = new HashMap<>();
                // If no images, set the default image
                picUrls.put(0, new ArrayList<String>() {{
                    add(Config.DEFAULT_CROWD_PIC_URL);
                    add("");
                }});

                if (jsonArray != null && jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject obj = jsonArray.getJSONObject(i);
                        final String url = obj.getString("image_url");
                        final String show = obj.getString("show");
                        final String timeAgo = getTimeAgo(url);
                        picUrls.put(i, new ArrayList<String>() {{
                            add(url);
                            add(timeAgo);
                            add(show);
                        }});
                    }
                }

                return picUrls;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

    public void buildGoogleLocationApi(Context context) {
        // Create instance of GoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
}

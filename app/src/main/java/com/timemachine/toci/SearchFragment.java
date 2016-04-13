package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


public class SearchFragment extends Fragment {

    /**
     * LogCat
     */
    private static final String TAG = SearchFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_TITLE = "SearchFragment";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        // Could add some parameters here that we wish to initialize
        // during the instantiation of this fragment.
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        final EditText mainEditText = (EditText) rootView.findViewById(R.id.enter_city);
        final ImageView mainButton = (ImageView) rootView.findViewById(R.id.main_btn);
        mainButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desired_city = mainEditText.getText().toString();

                /**
                 * AsyncTask which makes call to server and returns
                 * true if city found, false if city not found.
                 */
                new CheckCityTask() {
                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {

                            StartCityActivity(desired_city);

                        } else {

                            Toast.makeText(getActivity(), "City not found", Toast.LENGTH_LONG).show();

                        }
                    }
                }.execute(desired_city);

            }
        });

        return rootView;
    }

    /**
     * Method which starts the CityActivity and passes the city for
     * loading the CityFragment.
     * @param city
     */
    public void StartCityActivity(String city) {
        Intent intent = new Intent(getActivity(), CityActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((HomeMaterialActivity) context).onSectionAttached(
                ARG_SECTION_TITLE);

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "Detached");
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

    /**
     * AsyncTask which checks if city exists and returns boolean
     */
    private class CheckCityTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground (String... params) {

            String response;
            String city = params[0];

            try {
                String link = "http://crowdzeeker.com/AppCrowdZeeker/checkcity.php?city=" + URLEncoder.encode(city) + "";
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                Log.d(TAG, city + " found: " + response);

                return Boolean.valueOf(response);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute (Boolean result) {

        }
    }
}
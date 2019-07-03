package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
public class SearchFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_CITIES_LIST = "citiesListParam";
    private static final String SECTION_TITLE = "SearchFragment";
    private static final String SHOWCASE_ID = "custom example";
    private OnFragmentInteractionListener mListener;
    private Network network; // For checking if network is online
    private AutoCompleteTextView mainEditText;
    private CitiesSuggestionAdapter citySuggestAdapter;
    private String searchCity;
    private ImageView mainButton;

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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int position);
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(List<String> citiesListParam) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_CITIES_LIST, new ArrayList<>(citiesListParam));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To check online/offline
        network = new Network(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mainEditText = rootView.findViewById(R.id.enter_city);
        mainButton = rootView.findViewById(R.id.main_btn);

        // Get list of cities then build AutoCompleteTextView
        new GetCities(getContext()) {
            @Override
            protected void onPostExecute(Result result) {
                if (result != null && result.mResultValue != null) {
                     citySuggestAdapter = new CitiesSuggestionAdapter(getContext(),
                            android.R.layout.simple_dropdown_item_1line, result.mResultValue);
                    mainEditText.setAdapter(citySuggestAdapter);
                }
            }
        }.execute();

        mainButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide the keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!network.isOnline()) {
                    Toast.makeText(getContext().getApplicationContext(), R.string.error_offline,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                /**
                 * AsyncTask which makes call to server and returns
                 * true if city found, false if city not found.
                 */
                searchCity = mainEditText.getText().toString();

                // Set searchTerm to first suggestion if searchTerm contains suggestion
                if (!searchCity.isEmpty() && citySuggestAdapter.getCount() > 0) {
                    final Object item = citySuggestAdapter.getItem(0);
                    if (item.toString().contains(searchCity)) {
                        searchCity = item.toString();
                    }
                }


                if (!searchCity.isEmpty()) {
                    new CheckCityTask() {
                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (result) {
                                StartCityActivity(searchCity);
                            } else {
                                Snackbar noCityFoundDialog = Snackbar.make(getActivity().findViewById(R.id.snackBar),
                                        "No Crowds Found.", Snackbar.LENGTH_LONG);
                                noCityFoundDialog.setAction("Add Crowd", new AddCrowdListener());
                                noCityFoundDialog.show();
                            }
                        }
                    }.execute(searchCity);
                }
            }
        });

        // Start demo
        presentShowCaseView();

        return rootView;
    }

    /**
     * Method which starts the Activity and passes the city for
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

        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * AsyncTask which checks if city exists and returns boolean
     */
    private class CheckCityTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground (String... params) {

            String response;
            String city, state;
            String[] query = params[0].split(",");
            city = query[0];
            state = query.length == 1 ? "" : query[1].trim();

            try {
                String link = Config.CHECK_CITY_FOR_CROWDS_URL + "?" + "state=" + URLEncoder.encode(state, "UTF-8") +
                        "&city=" + URLEncoder.encode(city, "UTF-8");
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);

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

    public class AddCrowdListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int addCrowdFragPosition = 2;
            mListener.onFragmentInteraction(addCrowdFragPosition);
        }
    }

    private void presentShowCaseView() {
        // sequence example
        int withDelay = 500;
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(withDelay); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(mainEditText,
                "To search for crowds near you, type the city you're interested in.", "GOT IT");

        sequence.addSequenceItem(mainButton,
                "Then tap here to see what's happening!", "GOT IT");

        sequence.start();
    }
}

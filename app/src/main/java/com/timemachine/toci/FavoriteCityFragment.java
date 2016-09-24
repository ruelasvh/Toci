package com.timemachine.toci;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteCityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteCityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteCityFragment extends Fragment {
    // helper in debugging
    private final static String TAG = FavoriteCityFragment.class.getSimpleName();
    // the fragment initialization parameters
    private static final String CITY = "cityHolder";

    private String mCity;

    // Adapter which loads crowds
    private LiveCrowdRowAdapterv2 mLiveCrowdRowAdapterv2;
    // ListView that will hold the crowds
    private ListView mListView;
    // Spinner which shows while adapter loads cards
    private ProgressBar mProgressBar;
    // Widget used for pull-to-refresh ListView
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;


    public FavoriteCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param city Parameter 1.
     * @return A new instance of fragment FavoriteCityFragment.
     */
    public static FavoriteCityFragment newInstance(String city) {
        FavoriteCityFragment fragment = new FavoriteCityFragment();
        Bundle args = new Bundle();
        args.putString(CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it.
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCity = getArguments().getString(CITY);
            setFragmentTitle(getActivity(), mCity);
        }

        mContext = getActivity().getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_city, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor,
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Populate the view with list of cards
        initiateRefresh();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                initiateRefresh();
                return true;
            case R.id.action_favorite:
                mAppPrefs.setFavorite_city(mCity);
                if (mAppPrefs.getFavorite_cities() != null) {
                    for (String city : mAppPrefs.getFavorite_cities()) {
                        Log.i(TAG, city);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * By abstracting the refresh process to a single method, the app
     * allows both the SwipeGestureLayout onRefresh() method and the
     * Refresh action item to refresh the content.
     */
    private void initiateRefresh() {

        new GetCrowdsv2(new GetCrowdsv2.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(LiveCrowdRow[] crowds) {

                mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);
                mProgressBar.setVisibility(View.VISIBLE);
                mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);

                mLiveCrowdRowAdapterv2 = new LiveCrowdRowAdapterv2(getActivity(), R.layout.row, crowds);
                mLiveCrowdRowAdapterv2.notifyDataSetChanged();
                if (!mLiveCrowdRowAdapterv2.isEmpty()) mProgressBar.setVisibility(View.GONE);
                mListView.setAdapter(mLiveCrowdRowAdapterv2);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }).execute(mCity);
    }

    public void setFragmentTitle(Context context, String city) {
        ((HomeMaterialActivity) context).onSectionAttached(city);
    }
// End of FavoriteCityFragment class
}

package com.timemachine.toci;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    // the fragment initialization parameters
    private static final String CITY = "cityHolder";

    private String mCity;

    // Adapter which loads crowds
    private liveCrowdRowAdapterv2 mLiveCrowdRowAdapterv2;
    // ListView that will hold the crowds
    private ListView mListView;
    // Spinner which shows while adapter loads cards
    private ProgressBar mProgressBar;
    // Widget used for pull-to-refresh ListView
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    public FavoriteCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param _city Parameter 1.
     * @return A new instance of fragment FavoriteCityFragment.
     */
    public static FavoriteCityFragment newInstance(String _city) {
        FavoriteCityFragment fragment = new FavoriteCityFragment();
        Bundle args = new Bundle();
        args.putString(CITY, _city);
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
        }
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
        // Pass city name to main home activity to utilize in labeling
        // fragment upon attaching.
        ((HomeMaterialActivity) context).onSectionAttached(mCity);
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
            public void onAsyncTaskFinish(liveCrowdRow[] crowds) {

                mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);
                mProgressBar.setVisibility(View.VISIBLE);
                mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);

                mLiveCrowdRowAdapterv2 = new liveCrowdRowAdapterv2(getActivity(), R.layout.row, crowds);
                mLiveCrowdRowAdapterv2.notifyDataSetChanged();
                if (!mLiveCrowdRowAdapterv2.isEmpty()) mProgressBar.setVisibility(View.GONE);
                mListView.setAdapter(mLiveCrowdRowAdapterv2);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }).execute(mCity);
    }
// End of FavoriteCityFragment class
}

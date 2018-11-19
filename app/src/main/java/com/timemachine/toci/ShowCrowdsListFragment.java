package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Victor Ruelas on 9/24/16.
 */
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowCrowdsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCrowdsListFragment extends Fragment {

    private static final String ARG_FETCH_CROWDS_PARAM_FILTER = "fetchCrowdsParamFilter";
    private static final String ARG_FETCH_CROWDS_PARAM = "fetchCrowdsParam";
    private String FETCH_CROWDS_FILTER;
    private String FETCH_CROWDS;
    // Used for setting Fragment's title in parent activity
    private static final String SECTION_TITLE = "ShowCrowdsListFragment";
    private ArrayList<String> crowdList;
    private BaseAdapter mListAdapter;  // Adapter which loads crowds
    private ListView mListView; // ListView which will hold the crowds
    private ProgressBar mProgressBar; // Spinner which shows while adapter loads crowds
    private SwipeRefreshLayout mSwipeRefreshLayout; // Widget used for pull-to-refresh ListView
    private OnFragmentInteractionListener mListener; // Interface to help communicate up to the parent activity
    private GetCrowds getCrowdsTask; // AsyncTask to fetch crowds
    private Network network; // Class to check network status
    Context mContext;

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

    public ShowCrowdsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fetchCrowdsParamFilter fetch crowds by city or id.
     * @param fetchCrowdsParam city or ids.
     * @return A new instance of fragment ShowCrowdsListFragment.
     */
    public static ShowCrowdsListFragment newInstance(String fetchCrowdsParamFilter, String fetchCrowdsParam) {
        ShowCrowdsListFragment fragment = new ShowCrowdsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FETCH_CROWDS_PARAM_FILTER, fetchCrowdsParamFilter);
        args.putString(ARG_FETCH_CROWDS_PARAM, fetchCrowdsParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        // Assign value passed from parent activity to mParam1
        if (getArguments() != null) {
            FETCH_CROWDS_FILTER = getArguments().getString(ARG_FETCH_CROWDS_PARAM_FILTER);
            FETCH_CROWDS = getArguments().getString(ARG_FETCH_CROWDS_PARAM);
        }

        network = new Network(mContext);
        getCrowdsTask = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_crowds_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor,
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Populate the view with list of crowds
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);
        mProgressBar.setVisibility(View.VISIBLE);
        mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCrowds();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_crowds, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (getCrowdsTask != null && getCrowdsTask.getStatus() == AsyncTask.Status.RUNNING) {
                    return true;
                } else {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshCrowds();
                }
                return true;
            case R.id.action_show_map:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment mapFragment = ShowCrowdsMapFragment.newInstance(FETCH_CROWDS_FILTER, FETCH_CROWDS, crowdList);
                fragmentManager.beginTransaction().replace(R.id.container, mapFragment)
                        .addToBackStack(null).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onResume() {
        super.onResume();
        // Resume with updating crowds
        if (mProgressBar.getVisibility() != View.VISIBLE) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        refreshCrowds();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cancel updating crowds
        cancelRefreshCrowds();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void refreshCrowds() {

        if (network.isOnline()) {
            getCrowdsTask = new GetCrowds(getActivity(), new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    crowdList = SerializeLiveCrowd.toJson(crowds);

                    if (mListView.getAdapter() == null) {
                        if (mContext instanceof HomeMaterialActivity) {
                            mListAdapter = new SwipeableLiveCrowdListAdapter(mContext, R.layout.card_swipable, crowds);

                        } else if (mContext instanceof CityActivity) {
                            mListAdapter = new LiveCrowdListAdapter(mContext, R.layout.card, crowds);
                        }
                        mListView.setAdapter(mListAdapter);
                    } else {
                        if (mContext instanceof HomeMaterialActivity) {
                            ((SwipeableLiveCrowdListAdapter) mListAdapter).updateList(crowds);

                        } else if (mContext instanceof CityActivity) {
                            ((LiveCrowdListAdapter) mListAdapter).updateList(crowds);
                        }
                        mListAdapter.notifyDataSetChanged();
                    }
                }
            });
            getCrowdsTask.execute(FETCH_CROWDS_FILTER, FETCH_CROWDS);
        } else {
            Toast.makeText(getContext().getApplicationContext(), R.string.error_offline,
                    Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void cancelRefreshCrowds() {
        if (getCrowdsTask != null) {
            getCrowdsTask.cancel(true);
        }
    }
}

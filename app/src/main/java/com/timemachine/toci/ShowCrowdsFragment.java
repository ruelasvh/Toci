package com.timemachine.toci;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowCrowdsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCrowdsFragment extends Fragment {
    // Tag to be used for debugging
    private static final String TAG = ShowCrowdsFragment.class.getSimpleName();
    // Filter crowds by id
    private final static String BY_ID = "BY_ID";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FETCH_CROWDS_PARAM = "fetchCrowdsParam";
    // TODO: Rename and change types of parameters
    private String FETCH_CROWDS_FILTER;
    // Used for setting Fragment's title in parent activity
    private static final String SECTION_TITLE = "ShowCrowdsFragment";
    // Adapter which loads crowds
    private SwipeableLiveCrowdListAdapter mListAdapter;
    // ListView which will hold the crowds
    private ListView mListView;
    // Spinner which shows while adapter loads crowds
    private ProgressBar mProgressBar;
    // Widget used for pull-to-refresh ListView
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // Interface to help communicate up to the parent activity
    private OnFragmentSelectedListener mListener;
    // AsyncTask to fetch crowds
    private GetCrowds getCrowdsTask;
    // Class to check network status
    private Network network;
    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;

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
    public interface OnFragmentSelectedListener {
        void onNavDrawerItemSelected(int position);
    }

    public ShowCrowdsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
     * @return A new instance of fragment ShowCrowdsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCrowdsFragment newInstance(String fetchCrowdsParam) {
        ShowCrowdsFragment fragment = new ShowCrowdsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FETCH_CROWDS_PARAM, fetchCrowdsParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);

        // Assign value passed from parent activity to mParam1
        if (getArguments() != null) {
            FETCH_CROWDS_FILTER = getArguments().getString(ARG_FETCH_CROWDS_PARAM);
        }

        network = new Network(getContext());
        getCrowdsTask = null;

        // Set up preferences
        mContext = getContext();
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
                Fragment mapFragment = new ShowCrowdsMapFragment();
                fragmentManager.beginTransaction().replace(R.id.container, mapFragment)
                        .addToBackStack(null).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Set Fragment's title in parent activity
        ((HomeMaterialActivity) context).onSectionAttached(SECTION_TITLE);

        try {
            mListener = (OnFragmentSelectedListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentSelectedListener");
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
        String crowdIds = TextUtils.join(",", mAppPrefs.getFavorite_crowds());

        if (network.isOnline()) {
            getCrowdsTask = new GetCrowds(getActivity(), new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (mListView.getAdapter() == null) {
                        mListAdapter = new SwipeableLiveCrowdListAdapter(getContext(), R.layout.rowv2, crowds);
                        mListView.setAdapter(mListAdapter);
                    } else {
                        mListAdapter.updateList(crowds);
                        mListAdapter.notifyDataSetChanged();
                    }
                }
            });
            getCrowdsTask.execute(FETCH_CROWDS_FILTER, crowdIds);
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

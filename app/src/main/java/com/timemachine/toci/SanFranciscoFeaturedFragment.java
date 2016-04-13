package com.timemachine.toci;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by victorhugo on 5/24/15.
 */
public class SanFranciscoFeaturedFragment extends Fragment {

    private static final String ARG_SECTION_TITLE = "SanFranciscoFeaturedFragment";

    private static final String FROM_CITY = "San Francisco";

    private liveCrowdRowAdapterv2 adapter;
    private ProgressBar spinner;
    private ListView crowdList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public SanFranciscoFeaturedFragment() {
        // Required empty public constructor
    }

    public static SanFranciscoFeaturedFragment newInstance() {
        SanFranciscoFeaturedFragment fragment = new SanFranciscoFeaturedFragment();
        // Could add some parameters here that we wish to initialize
        // during the instantiation of this fragment.
        return fragment;
    }

    /*
     * Start the fragment lifecycle here.
     */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((HomeMaterialActivity) context).onSectionAttached(
                ARG_SECTION_TITLE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_city_featured, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        // BEGIN_INCLUDE (change_colors)
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

    /**
     * By abstracting the refresh process to a single method, the app
     * allows both the SwipeGestureLayout onRefresh() method and the
     * Refresh action item to refresh the content.
     */

    private void initiateRefresh() {

        new GetCrowdsv2(new GetCrowdsv2.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(liveCrowdRow[] crowds) {

                spinner = (ProgressBar) getActivity().findViewById(R.id.spinner);
                spinner.setVisibility(View.VISIBLE);
                crowdList = (ListView) getActivity().findViewById(R.id.crowds_listview);

                adapter = new liveCrowdRowAdapterv2(getActivity(), R.layout.row, crowds);
                adapter.notifyDataSetChanged();
                if (!adapter.isEmpty()) spinner.setVisibility(View.GONE);
                crowdList.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }).execute(FROM_CITY);
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

    /*
     * End of the start of the lifecycle. But could add the following:
     */

//    onStart() is called once the fragment is ready to be displayed on screen.
//    onResume() Allocate “expensive” resources such as registering for location, sensor updates, etc.
//    onPause() Release “expensive” resources. Commit any changes.
//    onDestroyView() is called when fragment's view is being destroyed, but the fragment is still kept around.
//    onDestroy() is called when fragment is no longer in use.
//    onDetach() is called when fragment is no longer connected to the activity.

    /*
     * End of the fragment's lifecycle
     */

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
}


package com.timemachine.toci;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by victorhugo on 5/24/15.
 */
public class SanJoseFeaturedFragment extends Fragment {

    private static final String ARG_SECTION_TITLE = "SanJoseFeaturedFragment";

    private static final String ARG_SECTION_CITY = "San Jose";

    // Strings to call the webservice and root directory of pictures
    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowdpics.php";
    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/";

    private liveCrowdRow[] crowds;
    private liveCrowdRowAdapter adapter;

    public SanJoseFeaturedFragment() {
        // Required empty public constructor
    }

    public static SanJoseFeaturedFragment newInstance() {
        SanJoseFeaturedFragment fragment = new SanJoseFeaturedFragment();
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
        return inflater.inflate(R.layout.fragment_city_featured, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View rootView = view;
        new GetCrowds(new GetCrowds.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(ArrayList<HashMap<String, String>> output) {

                ListView listView1 = (ListView) rootView.findViewById(R.id.crowds_listview);

                crowds = new liveCrowdRow[output.size()];

                for(int i = 0; i < output.size(); i++) {
                    crowds[i] = new liveCrowdRow(sortScript, output.get(i).get("name"), "", "",
                            LivePicsGalleryActivity.class);
                }

                adapter = new liveCrowdRowAdapter(getActivity(), R.layout.row, crowds);
                adapter.notifyDataSetChanged();
                listView1.setAdapter(adapter);

            }
        }).execute(ARG_SECTION_CITY);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        // TODO: handle refresh item selection in actionbar
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // refreshCrowds();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

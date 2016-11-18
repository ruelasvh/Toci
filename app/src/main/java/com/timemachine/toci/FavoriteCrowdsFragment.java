package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteCrowdsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteCrowdsFragment extends Fragment {
    // Tag to be used for debugging
    private static final String TAG = FavoriteCrowdsFragment.class.getSimpleName();
    // Filter crowds by id
    private final static String ID_FILTER = "ID";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters
    private ArrayList<String> mParam1;
    // Used for setting Fragment's title in parent activity
    private static final String SECTION_TITLE = "FavoriteCrowdsFragment";
    // Adapter which loads crowds
    private ListAdapter mListAdapter;
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
    // Variables used for showing link/icon to SearchFragment when crowds' list is empty
    private ImageButton searchButton;
    private TextView searchText;
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

    public FavoriteCrowdsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
     * @return A new instance of fragment FavoriteCrowdsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteCrowdsFragment newInstance(/*Set<String> param1*/) {
        FavoriteCrowdsFragment fragment = new FavoriteCrowdsFragment();
        Bundle args = new Bundle();
//        args.putStringArrayList(ARG_PARAM1, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);

        // Assign value passed from parent activity to mParam1
//        if (getArguments() != null) {
//            mParam1 = getArguments().getStringArrayList(ARG_PARAM1);
//        }

        // Set up preferences
        mContext = getContext();
        mAppPrefs = new AppPrefs(mContext);

        searchButton = new ImageButton(getActivity());
        searchText = new TextView(getActivity());
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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCrowds();
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
                refreshCrowds();
                return true;
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

        refreshCrowds();
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelRefreshCrowds();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void refreshCrowds() {

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);
        mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);

        if (!mAppPrefs.getFavorite_crowds().isEmpty()) {
            String crowdsIdString = TextUtils.join(",", mAppPrefs.getFavorite_crowds());

            getCrowdsTask = new GetCrowds(new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    mProgressBar.setVisibility(View.VISIBLE);

                    mListAdapter = new ListAdapter(crowds);
                    mListAdapter.notifyDataSetChanged();
                    if (!mListAdapter.isEmpty()) mProgressBar.setVisibility(View.GONE);
                    mListView.setAdapter(mListAdapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            getCrowdsTask.execute(ID_FILTER, crowdsIdString);
        }
        else {
            mListView.setAdapter(null);
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);

            showSearchCrowds(true);
        }
    }

    private void cancelRefreshCrowds() {
        getCrowdsTask.cancel(true);
    }

    private void showSearchCrowds(boolean show) {

        RelativeLayout rootLayout = (RelativeLayout)getActivity().findViewById(R.id.root);
        rootLayout.removeView(searchButton);
        rootLayout.removeView(searchText);

        if (show) {
            searchButton.setVisibility(View.VISIBLE);
            searchButton.setId('1');
            searchButton.setBackgroundColor(getResources().getColor(R.color.transparent));
            searchButton.setImageResource(R.drawable.ic_search_white_48dp);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Launch Fragment
                    int searchFragPosition = 0;
                    mListener.onNavDrawerItemSelected(searchFragPosition);
                }
            });

            searchText.setVisibility(View.VISIBLE);
            searchText.setId('0');
            searchText.setText("Search");
            searchText.setTextColor(getResources().getColor(R.color.white));
            searchText.setTextSize(24.f);
//            searchText.setTypeface(null, Typeface.BOLD);

            rootLayout.setGravity(Gravity.CENTER);
            rootLayout.addView(searchButton);
            rootLayout.addView(searchText);

            RelativeLayout.LayoutParams layoutParamsSearchButton = (RelativeLayout.LayoutParams) searchButton.getLayoutParams();

            layoutParamsSearchButton.addRule(RelativeLayout.ABOVE, searchText.getId());
            layoutParamsSearchButton.addRule(RelativeLayout.CENTER_IN_PARENT);
            searchButton.setLayoutParams(layoutParamsSearchButton);

            RelativeLayout.LayoutParams layoutParamsSearchText = (RelativeLayout.LayoutParams) searchText.getLayoutParams();
            layoutParamsSearchText.addRule(RelativeLayout.CENTER_IN_PARENT);
            searchText.setLayoutParams(layoutParamsSearchText);
        }

        if (!show) {
            searchButton.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
        }
    }

    class ListAdapter extends BaseAdapter {

        List<LiveCrowd> crowdsList;

        public ListAdapter(LiveCrowd[] crowds) {
            this.crowdsList = new ArrayList<>(Arrays.asList(crowds));
        }

        @Override
        public int getCount() {
            return crowdsList.size();
        }

        @Override
        public Object getItem(int position) {
            return crowdsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.rowv2, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                Picasso.with(getContext()).cancelRequest(viewHolder.livepic);
            }

            final LiveCrowd crowd = crowdsList.get(position);
            viewHolder.title.setText(crowd.getTitle());
            viewHolder.timeago.setText(crowd.getTimeago());
            viewHolder.distance.setText(crowd.getDistance());
            viewHolder.livepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LivePicsGalleryActivity.class);
                    intent.putExtra("crowd", SerializeLiveCrowd.toJson(crowd));
                    getContext().startActivity(intent);
                }
            });

            Picasso.with(getContext()).load( crowd.getPicUrls().get( crowd.getPicUrls().size() - 1).get(0) )
                    .into( viewHolder.livepic);

            final CardView cardView = viewHolder.cardView;
            final CoordinatorLayout coordinatorLayout = viewHolder.coordinatorLayout;
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) cardView.getLayoutParams();
            final SwipeDismissBehavior<CardView> behavior = new SwipeDismissBehavior<>();
            behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_END_TO_START);
            behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {

                @Override
                public void onDismiss(final View view) {

                    crowdsList.remove(position);
                    mAppPrefs.removeFavorite_crowd(crowd.getId());
                    notifyDataSetChanged();

                    if (crowdsList.isEmpty()) {
                        showSearchCrowds(true);
                    }

                    Snackbar.make(getActivity().findViewById(R.id.crowds_listview),
                            "Removed " + crowd.getTitle(), Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showSearchCrowds(false);
                                    crowdsList.add(position, crowd);
                                    mAppPrefs.setFavorite_crowd(crowd.getId());
                                    notifyDataSetChanged();
                              }
                            })
                            .show();
                }

                @Override
                public void onDragStateChanged(int i) {
                }
            });
            params.setBehavior(behavior);

            coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return behavior.onTouchEvent(coordinatorLayout, cardView, event);
                }
            });

            return convertView;
        }

        class ViewHolder {

            CoordinatorLayout coordinatorLayout;
            CardView cardView;
            ImageButton livepic;
            TextView title;
            TextView timeago;
            TextView distance;

            public ViewHolder(View convertView) {
                coordinatorLayout = (CoordinatorLayout) convertView.findViewById(R.id.coordinatorLayout);
                cardView = (CardView) convertView.findViewById(R.id.cardView);
                livepic = (ImageButton) convertView.findViewById(R.id.livepic);
                title = (TextView) convertView.findViewById(R.id.title);
                timeago = (TextView) convertView.findViewById(R.id.timeago);
                distance = (TextView) convertView.findViewById(R.id.distance);
            }
        }
    }
}

package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
    private static final String TAG = HomeMaterialActivity.class.getSimpleName();
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

    private OnFragmentInteractionListener mListener;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;

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
        displayCrowds();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayCrowds();
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
                displayCrowds();
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

    private void displayCrowds() {
//        LiveCrowd[] crowds = SerializeLiveCrowd.fromJson(mParam1);
        LiveCrowd[] crowds = SerializeLiveCrowd.fromJson(new ArrayList<>(mAppPrefs.getFav_crowds()));

//        Log.d(TAG, Integer.toString(crowds.length));

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);

        if (crowds.length != 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);

            mListAdapter = new ListAdapter(crowds);
            mListAdapter.notifyDataSetChanged();
            if (!mListAdapter.isEmpty()) mProgressBar.setVisibility(View.GONE);
            mListView.setAdapter(mListAdapter);
            mSwipeRefreshLayout.setRefreshing(false);


        }
        else {
            mProgressBar.setVisibility(View.GONE);

            final ImageButton searchButton = new ImageButton(getActivity());
            searchButton.setId('1');
            searchButton.setBackgroundColor(getResources().getColor(R.color.transparent));
            searchButton.setImageResource(R.drawable.ic_search_white_48dp);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Launch Fragment
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, new SearchFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            final TextView searchText = new TextView(getActivity());
            searchText.setId('0');
            searchText.setText("Search");
            searchText.setTextColor(getResources().getColor(R.color.white));
            searchText.setTextSize(24.f);
//            searchText.setTypeface(null, Typeface.BOLD);


            RelativeLayout rootLayout = (RelativeLayout)getActivity().findViewById(R.id.root);
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
                    mAppPrefs.removeFav_crowd(crowd);
                    notifyDataSetChanged();
                    // Debug
                    if (mAppPrefs.getFav_crowds() != null) {
                        for (String element : mAppPrefs.getFav_crowds()) {
                            Log.i(TAG, element);
                        }
                    }                    Snackbar.make(getActivity().findViewById(R.id.crowds_listview),
                            "Removed " + crowd.getTitle(), Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    crowdsList.add(position, crowd);
//                                    mAppPrefs.setFav_crowd(crowd);
                                    notifyDataSetChanged();
                                    // Debug
                                    if (mAppPrefs.getFav_crowds() != null) {
                                        for (String element : mAppPrefs.getFav_crowds()) {
                                            Log.i(TAG, element);
                                        }
                                    }                                }
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

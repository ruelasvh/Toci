package com.timemachine.toci;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteCrowdsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteCrowdsFragment extends Fragment {
    private static final String TAG = HomeMaterialActivity.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private ArrayList<String> mParam1;

    // Adapter which loads crowds
    private LiveCrowdRowAdapterv2 mLiveCrowdRowAdapterv2;
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
    public static FavoriteCrowdsFragment newInstance(Set<String> param1) {
        FavoriteCrowdsFragment fragment = new FavoriteCrowdsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getStringArrayList(ARG_PARAM1);
            // Set fragment's title
            setFragmentTitle(getActivity(), "Favorite Crowds");
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FavoriteCityFragment.OnFragmentInteractionListener) {
            mListener = (FavoriteCrowdsFragment.OnFragmentInteractionListener) context;
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

    public void setFragmentTitle(Context context, String title) {
        ((HomeMaterialActivity) context).onSectionAttached(title);
    }

    private void displayCrowds() {
        LiveCrowdRow[] crowds = SerializeLiveCrowdRow.fromJson(mParam1);

//        Log.d(TAG, Integer.toString(crowds.length));

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.spinner);


        if (crowds.length != 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView = (ListView) getActivity().findViewById(R.id.crowds_listview);

            mLiveCrowdRowAdapterv2 = new LiveCrowdRowAdapterv2(getActivity(), R.layout.row, crowds);
            mLiveCrowdRowAdapterv2.notifyDataSetChanged();
            if (!mLiveCrowdRowAdapterv2.isEmpty()) mProgressBar.setVisibility(View.GONE);
            mListView.setAdapter(mLiveCrowdRowAdapterv2);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            Button search = new Button(getActivity());
            search.setText("Search Crowds");
            RelativeLayout rootLayout = (RelativeLayout)getActivity().findViewById(R.id.root);
            rootLayout.addView(search);
        }
    }
}

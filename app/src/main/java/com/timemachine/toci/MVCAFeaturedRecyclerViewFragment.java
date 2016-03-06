package com.timemachine.toci;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * Created by victorhugo on 2/17/16.
 * CrowdZeeker, LLC
 * Copyright (c) 2016 CrowdZeeker, LLC All rights reserved.
 */
public class MVCAFeaturedRecyclerViewFragment extends BaseListFragment {

    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowd.php";
    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/testcrowdpics/";


    private View rootView;

    private CardArrayRecyclerViewAdapter mCardArrayRecyclerViewAdapter;

    /**
     * The {@link android.support.v4.widget.SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Constructor
    public MVCAFeaturedRecyclerViewFragment() {
        // Empty constructor
    }

    /*
     * Override inherited methods
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.crowdcard_list_recyview, container, false);
        setupListFragment(rootView);
        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor,
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor);
        // END_INCLUDE (change_colors)
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        hideList(false);

        ArrayList<Card> cards = new ArrayList<>();

        mCardArrayRecyclerViewAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        CardRecyclerView mRecyclerView = (CardRecyclerView) getActivity().findViewById(R.id.crowdCard_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayRecyclerViewAdapter);
        }
        // populate empty view
        initiateRefresh();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // repopulate empty view
                initiateRefresh();
            }
        });


    }

    /**
     * By abstracting the refresh process to a single method, the app
     * allows both the SwipeGestureLayout onRefresh() method and the
     * Refresh action item to refresh the content.
     */

    private void initiateRefresh() {
        /**
         * Execute the background task of loading crowdCards, which uses
         * {@link android.os.AsyncTask} to load the data
         */
        new LoaderAsyncTask().execute();
    }

    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which
     * updates the data in the CardArrayRecyclerViewAdapter and turns off
     * the refreshing indicator.
     */

    private void onRefreshComplete(ArrayList<Card> result) {

        // Clear the adapter to prevent duplicate entries
        mCardArrayRecyclerViewAdapter.clear();
        // Update the list with cards
        updateAdapter(result);
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Method to update the CardArrayRecyclerViewAdapter
     */
    private void updateAdapter(ArrayList<Card> cards) {
        if (cards != null) {
            mCardArrayRecyclerViewAdapter.addAll(cards);
            displayList();
        }
    }


    /**
     * Task {@link AsyncTask} which fetches updated crowdCards.
     */
    private class LoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Card>> {

        LoaderAsyncTask() {
            // Empty constructor
        }

        @Override
        protected ArrayList<Card> doInBackground(Void... params) {
            // Elaborate crowdCards
            if (isAdded()) {
                ArrayList<Card> cards = initCrowdList();
                return cards;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> cards) {

            // Tell the Fragment that the refresh has completed
            onRefreshComplete(cards);

        }
    }


    /**
     * Method builds a simple list of crowdCards
     */
    private ArrayList<Card> initCrowdList() {

        // Initialize an array of crowdCards
        ArrayList<Card> cards = new ArrayList<>();
        // Crowd #1
        final crowdCard card1 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card1.setCrowdTitle("Molly Magees", false);
        //card1.setCrowdSubtitle("Popular Bar");
        card1.setCrowdRatingComment("1 min away");
        card1.setCrowdCoverCharge("$$");
        card1.setCrowdRating(4.7f);
        //card1.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        // Add thumbnail to card from php script on server
        // Set true to use external library
        // Set the url
        card1.new HttpAsyncTask() {
            @Override
            public void onPostExecute(String picUrl) {
                card1.setCrowdSubtitle(picUrl);
            }
        }.execute(sortScript);
/*
        try {
            final crowdCard.UrlThumbnail thumbnail1 = new crowdCard.UrlThumbnail(getActivity());
            thumbnail1.setExternalUsage(true);
            String filename = new crowdCard.HttpAsyncTask().execute(sortScript).get().toString();
            thumbnail1.setUrl(imageBaseDirectory + filename);
            card1.addCardThumbnail(thumbnail1);
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }*/

        card1.setSpecialsHeader("Specials", getResources().getColor(R.color.violetSpecials));
        card1.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card1.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
        card1.setCrowdLivePics(GetCurrImageActivity.class);
        cards.add(card1);

        // Crowd #2
        crowdCard card2 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card2.setCrowdTitle("Stephens Green", false);
        card2.setCrowdSubtitle("Irish-style Pub");
        card2.setCrowdRatingComment("1 min away");
        card2.setCrowdCoverCharge("$$");
        card2.setCrowdRating(4.0f);
        card2.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1680.jpg");
        card2.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card2.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card2.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card2.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card2);

        // Crowd #3
        crowdCard card3 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card3.setCrowdTitle("Opal", false);
        card3.setCrowdSubtitle("Hip-hop Club");
        card3.setCrowdRatingComment("1 min away");
        card3.setCrowdCoverCharge("$$");
        card3.setCrowdRating(4.0f);
        card3.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1694.jpg");
        card3.setSpecialsHeader("Specials", getResources().getColor(R.color.yellowSpecials));
        card3.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card3.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
        card3.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card3);

        // Crowd #4
        crowdCard card4 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card4.setCrowdTitle("Monte Carlo", false);
        card4.setCrowdSubtitle("Latin Club");
        card4.setCrowdRatingComment("1 min away");
        card4.setCrowdCoverCharge("$$");
        card4.setCrowdRating(4.8f);
        card4.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1684.jpg");
        card4.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card4.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card4.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card4.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card4);

        // Crowd #5
        crowdCard card5 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card5.setCrowdTitle("Mervs", false);
        card5.setCrowdSubtitle("Dive Bar");
        card5.setCrowdRatingComment("1 min away");
        card5.setCrowdCoverCharge("$");
        card5.setCrowdRating(4.0f);
        card5.setCrowdLogoUrl("http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/IMG_20160224_142750.jpg");
        card5.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card5.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card5.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card5.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card5);

        return cards;
    }
}

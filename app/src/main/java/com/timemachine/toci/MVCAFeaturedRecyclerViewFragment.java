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

    private View rootView;

    CardArrayRecyclerViewAdapter mCardArrayRecyclerViewAdapter;

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
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayRecyclerViewAdapter);
        }

        //Load cards
        new LoaderAsyncTask().execute();
    }


    /**
     * Async Task to elaborate crowdCards
     */
    class LoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Card>> {

        LoaderAsyncTask() {
            // Empty constructor
        }

        @Override
        protected ArrayList<Card> doInBackground(Void... params) {
            // Elaborate crowdCards
            if (isAdded()) {
                ArrayList<Card> cards = initcrowdCard();
                return cards;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> cards) {
            // Update the adapter
            updateAdapter(cards);
        }
    }

    /**
     * Method builds a simple list of crowdCards
     */
    private ArrayList<Card> initcrowdCard() {

        // Initialize an array of crowdCards
        ArrayList<Card> cards = new ArrayList<>();
        // Crowd #1
        final crowdCard card1 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card1.setCrowdTitle("Molly Magees", false);
        card1.setCrowdSubtitle("Popular Bar");
        card1.setCrowdRatingComment("1 min away");
        card1.setCrowdCoverCharge("$$");
        card1.setCrowdRating(4.7f);
        card1.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card1.setSpecialsHeader("Specials", getResources().getColor(R.color.violetSpecials));
        card1.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card1.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
        card1.setCrowdLivePics(LivePicsGalleryActivity.class);
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
        card5.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1727.jpg");
        card5.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card5.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card5.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card5.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card5);

        return cards;
    }

    /**
     * Method to update the adapter
     */
    private void updateAdapter(ArrayList<Card> cards) {
        if (cards != null) {
            mCardArrayRecyclerViewAdapter.addAll(cards);
            displayList();
        }
    }



}

/*
package com.timemachine.toci;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;


public class ExampleCardExpandFragment extends Fragment {

    @Override
    protected int getSubTitleHeaderResourceId() {
        return R.string.title_activity_mountain_view_ca;
    }

    @Override
    protected int getTitleHeaderResourceId() {
        return R.string.city_1;
    }

    @Override
    public int getTitleResourceId() {
        return R.string.title_activity_search_main;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mv_allcrowds_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCards();
    }

    private void initCards() {

        //Initiate an array of Cards
        ArrayList<Card> cards = new ArrayList<>();
        for (int 1=0; i<10; i++) {
            Card card = init_standard_header_with_expandcollapse_button_custom_area("Header "+i,i);
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    private Card init_standard_header_with_expandcollapse_button_custom_area(String titleHeader, int i) {
        //Create a Card
        Card card = new Card(getActivity());
        //Create a CardHeader
        CardHeader header = new CardHeader(getActivity());
        //Set the header title
        header.setTitle(titleHeader);
        //Set visible the exapand/collapse button
        header.setButtonExpandVisible(true);
        //Add Header to card
        card.addCardHeader(header);
        //This provides a simple (and useless) expand area
        CardExpand expand = new CardExpand(getActivity());
        //Add Expand Area to Card
        card.addCardExpand(expand);

        //Swipe
        card.setSwipeable(true);

    return card;

    }
}
*/
package com.timemachine.toci;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by victorhugo on 5/24/15.
 */
public class MVCAAllCrowdsFragment extends Fragment {

    // Public Constructor
    public MVCAAllCrowdsFragment() {
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mv_ca_allcrowds, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int listImages[] = new int[]{R.drawable.user, R.drawable.user, R.drawable.user,
                R.drawable.user, R.drawable.user};

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < 5; i++) {
            Card card = new Card(getActivity());
            CardHeader header = new CardHeader(getActivity());
            header.setTitle("Crowd: " + i);
            card.setTitle("Awesome Crowds");
            card.addCardHeader(header);

            CardThumbnail thumb = new CardThumbnail(getActivity());
            thumb.setDrawableResource(listImages[i]);
            card.addCardThumbnail(thumb);

            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardListView listView = (CardListView) rootView.findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }
}

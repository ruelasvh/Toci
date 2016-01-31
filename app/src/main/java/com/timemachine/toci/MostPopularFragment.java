package com.timemachine.toci;

import android.app.Activity;
//import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import it.gmariotti.cardslib.library.internal.Card;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MostPopularFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MostPopularFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MostPopularFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "sectionNumber";

    private String mSectionNumber;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    protected ScrollView mScrollView;

    private GoogleMap map;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment MostPopularFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MostPopularFragment newInstance(int sectionNumber) {
        MostPopularFragment fragment = new MostPopularFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MostPopularFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getString(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mostpopular, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScrollView = (ScrollView) getActivity().findViewById(R.id.crowdCard_scrollview);

        initCrowd1();
        initCrowd2();
        initCrowd3();
        initCrowd4();
        initCrowd5();
    }

    /**
     * Build crowdcards
     */

    private void initCrowd1() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("2 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_inside_3);
        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
        card.setCrowdMapExpand(R.layout.crowd_info_expand);
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {


                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();

                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //card.setCrowdLivePics(GetCurrImageActivity.class);
        card.setCrowdLivePics(LivePicsGalleryActivity.class);
        card.setCardinView(rootView, R.id.crowdCard1);
    }

    private void initCrowd2() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("2 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_front_main);
        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
        card.setCrowdMapExpand(R.layout.crowd_info_expand);
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {


                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();

                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //card.setCrowdLivePics(GetCurrImageActivity.class);
        card.setCrowdLivePics(LivePicsGalleryActivity.class);
        card.setCardinView(rootView, R.id.crowdCard2);
    }

    private void initCrowd3() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("3 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_inside_2);
        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
        card.setCrowdMapExpand(R.layout.crowd_info_expand);
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {


                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();

                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //card.setCrowdLivePics(GetCurrImageActivity.class);
        card.setCrowdLivePics(LivePicsGalleryActivity.class);
        card.setCardinView(rootView, R.id.crowdCard3);
    }

    private void initCrowd4() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("4 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_inside_1);
        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
        card.setCrowdMapExpand(R.layout.crowd_info_expand);
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {


                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();

                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //card.setCrowdLivePics(GetCurrImageActivity.class);
        card.setCrowdLivePics(LivePicsGalleryActivity.class);
        card.setCardinView(rootView, R.id.crowdCard4);
    }

    private void initCrowd5() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("5 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_front_main);
        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
        card.setCrowdMapExpand(R.layout.crowd_info_expand);
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {


                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();

                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //card.setCrowdLivePics(GetCurrImageActivity.class);
        card.setCrowdLivePics(LivePicsGalleryActivity.class);
        card.setCardinView(rootView, R.id.crowdCard5);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
        public void onFragmentInteraction(Uri uri);
    }

}

package com.timemachine.toci;

import android.app.Activity;
//import android.app.FragmentTransaction;
import android.graphics.Color;
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
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


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

    private CardListView mListView;
    private CardArrayAdapter mCardArrayAdapter;

    private GoogleMap map;

    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowd.php";


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

        //mScrollView = (ScrollView) getActivity().findViewById(R.id.crowdCard_scrollview);

        //initCrowd1();
        //initCrowd2();
        //initCrowd3();
        //initCrowd4();
        //initCrowd5();
        initCrowdList();
    }

    /**
     * Build crowdcards
     **/
/**
    private void initCrowd1() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("2 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_inside_3);
        card.setSpecialsHeader("Specials", getResources().getColor(R.color.violetSpecials));
        card.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
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
/**
    private void initCrowd2() {
        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
        card.setCrowdTitle("Molly Magees");
        card.setCrowdSubtitle("Famous Irish Pub");
        card.setCrowdRatingComment("2 min away");
        card.setCrowdCoverCharge("$$");
        card.setCrowdRating(4.7f);
        card.setCrowdLogo(R.drawable.mollys_front_main);
        card.setSpecialsHeader("Karaoke!", getResources().getColor(R.color.blueSpecials));
        card.setSpecials1("\u2022 Free 'til 12am", getResources().getColor(R.color.yellowSpecials));
        card.setSpecials2("\u2022 $4 Shots", getResources().getColor(R.color.yellowSpecials));
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
**/
    private void initCrowdList() {

        ArrayList<Card> cards = new ArrayList<>();

        // Crowd #1
        final crowdCard card1 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card1.setCrowdTitle("Molly Magees");
        card1.setCrowdSubtitle("Famous Irish Pub");
        card1.setCrowdRatingComment("1 min away");
        card1.setCrowdCoverCharge("$$");
        card1.setCrowdRating(4.7f);
        //card1.setCrowdLogo(R.drawable.mollys_front_main);
        card1.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card1.setSpecialsHeader("Specials", getResources().getColor(R.color.violetSpecials));
        card1.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card1.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
        card1.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.team_member_victor);
        //card1.setCrowdMapExpand(R.layout.crowd_info_expand);
        card1.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {

                card1.setCrowdPicUrl(sortScript);

                /*
                TextView address = (TextView) getActivity().findViewById(R.id.address);
                address.setText(getResources().getString(R.string.mtnview_street));
                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
                zipcode.setText(getResources().getString(R.string.mtnview_zip));

                MapsFragment mymap = new MapsFragment();
                LatLng mtnview = new LatLng(37.3894, -122.0819);
                mymap.setLocation(mtnview);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();
                */
                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        card1.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card1);

        // Crowd #2
        crowdCard card2 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card2.setCrowdTitle("Stephens Green");
        card2.setCrowdSubtitle("Irish-style Pub");
        card2.setCrowdRatingComment("1 min away");
        card2.setCrowdCoverCharge("$$");
        card2.setCrowdRating(4.0f);
        //card2.setCrowdLogo(R.drawable.mollys_inside_1);
        card2.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card2.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card2.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card2.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card2.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.team_member_victor);
        card2.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card2);

        // Crowd #3
        crowdCard card3 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card3.setCrowdTitle("Opal");
        card3.setCrowdSubtitle("Hip-hop Club");
        card3.setCrowdRatingComment("1 min away");
        card3.setCrowdCoverCharge("$$");
        card3.setCrowdRating(4.0f);
        //card3.setCrowdLogo(R.drawable.mollys_inside_2);
        card3.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card3.setSpecialsHeader("Specials", getResources().getColor(R.color.yellowSpecials));
        card3.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.redSpecials));
        card3.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.redSpecials));
        card3.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.team_member_roy);
        card3.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card3);

        // Crowd #4
        crowdCard card4 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card4.setCrowdTitle("Monte Carlo");
        card4.setCrowdSubtitle("Latin Club");
        card4.setCrowdRatingComment("1 min away");
        card4.setCrowdCoverCharge("$$");
        card4.setCrowdRating(4.8f);
        //card4.setCrowdLogo(R.drawable.mollys_inside_3);
        card4.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card4.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card4.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card4.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card4.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.team_member_gesem);
        card4.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card4);

        // Crowd #5
        crowdCard card5 = new crowdCard(this.getActivity(), R.layout.crowd_card);
        card5.setCrowdTitle("Mervs");
        card5.setCrowdSubtitle("Dive Bar");
        card5.setCrowdRatingComment("1 min away");
        card5.setCrowdCoverCharge("$");
        card5.setCrowdRating(4.0f);
        //card5.setCrowdLogo(R.drawable.mollys_inside_1);
        card5.setCrowdLogoUrl("http://www.mollysmtview.com/images/gal-9.jpg");
        card5.setSpecialsHeader("Specials", getResources().getColor(R.color.blueSpecials));
        card5.setSpecials1("\u2022 $2 BudLight", getResources().getColor(R.color.yellowSpecials));
        card5.setSpecials2("\u2022 $3 Shots", getResources().getColor(R.color.yellowSpecials));
        card5.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.team_member_victor);
        card5.setCrowdLivePics(LivePicsGalleryActivity.class);
        cards.add(card5);

        // Set the adapter
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        mListView = (CardListView) getActivity().findViewById(R.id.carddemo_extra_list_viewanimations);
        mListView.setAdapter(mCardArrayAdapter);
        // set animation
        if (mListView == null) {
            setBottomAdapter();
        }

    }

    // Bottom animation
    private void setBottomAdapter() {
        AnimationAdapter animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(mListView);
        mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
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

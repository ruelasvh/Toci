/*************** NOT WORKING *****************************/

package com.timemachine.toci;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PACAFeaturedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PACAFeaturedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PACAFeaturedFragment extends android.support.v4.app.Fragment implements OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "sectionNumber";

    // TODO: Rename and change types of parameters
    private String mSectionNumber;

    private OnFragmentInteractionListener mListener;

    // ListView to hold list of crowdCards
    private CardListView listView;

    // class to implement custom pull-to-refresh
    PullToRefreshLayout mPullToRefreshLayout;

    public static final int SIMULATED_REFRESH_LENGHT = 5000;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment PACAFeaturedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PACAFeaturedFragment newInstance(int sectionNumber) {
        PACAFeaturedFragment fragment = new PACAFeaturedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PACAFeaturedFragment() {
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
        return inflater.inflate(R.layout.fragment_pa_ca_featured, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (CardListView) getActivity().findViewById(R.id.carddemo_extra_list_actionbarpulltorefresh);

        initListCards();

        // Retrieve the PullToRefreshLayout from the content view
        mPullToRefreshLayout = (PullToRefreshLayout) getActivity().findViewById(R.id.carddemo_extra_ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getActivity())
                // Mark all children as pullable
                .allChildrenArePullable()
                // Set the OnRefreshListener
                .listener(this)
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
    }

    private void initListCards() {

        // Init an array of crowdCards
        ArrayList<Card> cards = new ArrayList<>();

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
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        /*
         Simulate refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SIMULATED_REFRESH_LENGHT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
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

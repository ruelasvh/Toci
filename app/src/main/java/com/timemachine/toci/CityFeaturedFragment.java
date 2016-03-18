package com.timemachine.toci;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by victorhugo on 5/24/15.
 */
public class CityFeaturedFragment extends Fragment /*implements GetCrowds.AsyncResponse*/ {

    private static final String ARG_SECTION_TITLE = "CityFeaturedFragment";

    private static final String ARG_SECTION_CITY = "City";

    // Strings to call the webservice and root directory of pictures
    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowdpics.php";
    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/";

    private liveCrowdRow[] crowds;
    private liveCrowdRowAdapter adapter;

    String result = "";

    public CityFeaturedFragment() {
        // Required empty public constructor
    }

    public static CityFeaturedFragment newInstance() {
        CityFeaturedFragment fragment = new CityFeaturedFragment();
        // Could add some parameters here that we wish to initialize
        // during the instantiation of this fragment.
        return fragment;
    }

    /*
     * Start the fragment lifecycle here.
     */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((HomeMaterialActivity) context).onSectionAttached(
                ARG_SECTION_TITLE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


//        new GetCrowds(this).execute(ARG_SECTION_CITY);

//        final liveCrowdRow[] crowds = new liveCrowdRow[2];
//
//

//        liveCrowdRow[] crowds = new liveCrowdRow[]{
//                new liveCrowdRow(sortScript, "Molly Magees", "Famous Irish Bar", "123 Castro St. 2.6mi",
//                        LivePicsGalleryActivity.class),
//                new liveCrowdRow(sortScript, "stephen's green", "Irish Bar", "124 Castro St. 2.6mi",
//                        Upload2DBActivity.class),
//                new liveCrowdRow(sortScript, "Opal", "Hip-hop Club", "122 Castro St. 2.6mi",
//                        LivePicsGalleryActivity.class),
//                new liveCrowdRow(sortScript, "monte carlo", "Latin Club", "341 Castro St. 2.6mi",
//                        LivePicsGalleryActivity.class),
//                new liveCrowdRow(sortScript, "merv's", "Dive Bar", " 2152 Columbus St. 2.6mi",
//                        LivePicsGalleryActivity.class),
//        };
//
//        adapter = new liveCrowdRowAdapter(getActivity(), R.layout.row, crowds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_featured, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View rootView = view;
        new GetCrowds(new GetCrowds.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(ArrayList<HashMap<String, String>> output) {

                ListView listView1 = (ListView) rootView.findViewById(R.id.crowds_listview);

                crowds = new liveCrowdRow[output.size()];

                for(int i = 0; i < output.size(); i++) {
                    crowds[i] = new liveCrowdRow(sortScript, output.get(i).get("name"), "", "",
                            LivePicsGalleryActivity.class);
                }

                adapter = new liveCrowdRowAdapter(getActivity(), R.layout.row, crowds);
                adapter.notifyDataSetChanged();
                listView1.setAdapter(adapter);

            }
        }).execute(ARG_SECTION_CITY);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

//    @Override
//    public void onAsyncTaskFinish(ArrayList<HashMap<String, String>> output) {
//
//        Toast.makeText(getContext(), output.get(0).get("name"), Toast.LENGTH_LONG)
//                .show();
//    }

    /*
     * End of the start of the lifecycle. But could add the following:
     */

//    onStart() is called once the fragment is ready to be displayed on screen.
//    onResume() Allocate “expensive” resources such as registering for location, sensor updates, etc.
//    onPause() Release “expensive” resources. Commit any changes.
//    onDestroyView() is called when fragment's view is being destroyed, but the fragment is still kept around.
//    onDestroy() is called when fragment is no longer in use.
//    onDetach() is called when fragment is no longer connected to the activity.

    /*
     * End of the fragment's lifecycle
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: handle refresh item selection in actionbar
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // refreshCrowds();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

    /**
     * Build crowdcards
     */

//    private void initcrowdCard1() {
//
//        final crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
//        card.setCrowdTitle("Crowd Title");
//        card.setCrowdSubtitle("The subtitle");
//        card.setCrowdRatingComment("5 min away");
//        card.setCrowdCoverCharge("Price");
//        card.setCrowdRating(4f);
//        //card.setBackgroundResource(getResources().getDrawable(R.drawable.mollys_front_main));
//        card.setCrowdPicUrl(sortScript);
//        //card.setBackgroundResource(getResources().getDrawable(R.drawable.mollys_inside_cropped));
//        //card.setShadow(true);
//        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.livePic, R.drawable.stephensgreenlive2);
//        card.setCrowdMapExpand(R.layout.crowd_info_expand);
//        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
//            @Override
//            public void onExpandEnd(Card card) {
//
//
//                TextView address = (TextView) getActivity().findViewById(R.id.address);
//                address.setText(getResources().getString(R.string.mtnview_street));
//                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
//                zipcode.setText(getResources().getString(R.string.mtnview_zip));
//
//                MapsFragment mymap = new MapsFragment();
//                LatLng mtnview = new LatLng(37.3894, -122.0819);
//                mymap.setLocation(mtnview);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();
//
//                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        //card.setCrowdLivePics(GetCurrImageActivity.class);
//        card.setCrowdLivePics(LivePicsGalleryActivity.class);
//        card.setCardinView(rootView, R.id.carddemo1);
//    }
//
//    private void initcrowdCard2() {
//        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
//        card.setCrowdTitle("Crowd Title",false);
//        card.setCrowdSubtitle("This is the subtitle");
//        card.setCrowdRatingComment("10 min away");
//        card.setCrowdCoverCharge("Price");
//        card.setCrowdRating(4f);
//        //card.setCrowdLogo(R.drawable.stephensgreenlive2);
//        card.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1680.jpg");
//        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
//        card.setCrowdMapExpand(R.layout.crowd_info_expand);
//        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
//            @Override
//            public void onExpandEnd(Card card) {
//
//
//                TextView address = (TextView) getActivity().findViewById(R.id.address);
//                address.setText(getResources().getString(R.string.mtnview_street));
//                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
//                zipcode.setText(getResources().getString(R.string.mtnview_zip));
//
//                MapsFragment mymap = new MapsFragment();
//                LatLng mtnview = new LatLng(37.3894, -122.0819);
//                mymap.setLocation(mtnview);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();
//
//                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        //card.setCrowdLivePics(GetCurrImageActivity.class);
//        card.setCrowdLivePics(LivePicsGalleryActivity.class);
//        card.setCardinView(rootView, R.id.carddemo2);
//    }
//
//    private void initcrowdCard3() {
//        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
//        card.setCrowdTitle("Crowd Title");
//        card.setCrowdSubtitle("This is the subtitle");
//        card.setCrowdRatingComment("11 min away");
//        card.setCrowdCoverCharge("Price");
//        card.setCrowdRating(4f);
//        //card.setCrowdLogo(R.drawable.stephensgreenlive2);
//        card.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1694.jpg");
//        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
//        card.setCrowdMapExpand(R.layout.crowd_info_expand);
//        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
//            @Override
//            public void onExpandEnd(Card card) {
//
//
//                TextView address = (TextView) getActivity().findViewById(R.id.address);
//                address.setText(getResources().getString(R.string.mtnview_street));
//                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
//                zipcode.setText(getResources().getString(R.string.mtnview_zip));
//
//                MapsFragment mymap = new MapsFragment();
//                LatLng mtnview = new LatLng(37.3894, -122.0819);
//                mymap.setLocation(mtnview);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();
//
//                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        //card.setCrowdLivePics(GetCurrImageActivity.class);
//        card.setCrowdLivePics(LivePicsGalleryActivity.class);
//        card.setCardinView(rootView, R.id.carddemo3);
//    }
//
//    private void initcrowdCard4() {
//        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
//        card.setCrowdTitle("Crowd Title");
//        card.setCrowdSubtitle("This is the subtitle");
//        card.setCrowdRatingComment("8 min away");
//        card.setCrowdCoverCharge("Price");
//        card.setCrowdRating(4f);
//        //card.setCrowdLogo(R.drawable.stephensgreenlive2);
//        card.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1684.jpg");
//        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
//        card.setCrowdMapExpand(R.layout.crowd_info_expand);
//        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
//            @Override
//            public void onExpandEnd(Card card) {
//
//
//                TextView address = (TextView) getActivity().findViewById(R.id.address);
//                address.setText(getResources().getString(R.string.mtnview_street));
//                TextView zipcode = (TextView) getActivity().findViewById(R.id.zipcode);
//                zipcode.setText(getResources().getString(R.string.mtnview_zip));
//
//                MapsFragment mymap = new MapsFragment();
//                LatLng mtnview = new LatLng(37.3894, -122.0819);
//                mymap.setLocation(mtnview);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.mapLayout, mymap).commit();
//
//                //Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        //card.setCrowdLivePics(GetCurrImageActivity.class);
//        card.setCrowdLivePics(LivePicsGalleryActivity.class);
//        card.setCardinView(rootView, R.id.carddemo4);
//    }
//
//    private void initcrowdCard5() {
//        crowdCard card = new crowdCard(getActivity(), R.layout.crowd_card);
//        card.setCrowdTitle("Crowd Title");
//        card.setCrowdSubtitle("This is the subtitle");
//        card.setCrowdRatingComment("9 min away");
//        card.setCrowdCoverCharge("Price");
//        card.setCrowdRating(4f);
//        //card.setCrowdLogo(R.drawable.stephensgreenlive2);
//        card.setCrowdLogoUrl("http://www.mollysmtview.com/images/img_1727.jpg");
//        //card.setCrowdExpand(R.layout.crowd_card_ratings_view, R.id.userPic, R.drawable.photo_user);
//        card.setCrowdMapExpand(R.layout.crowd_info_expand);
//
//        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
//            @Override
//            public void onExpandEnd(Card card) {
//
//
//                Toast.makeText(getActivity(),"Expand "+card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        card.setCrowdLivePics(GetCurrImageActivity.class);
//        //card.setCrowdLivePics(LivePicsGalleryActivity.class);
//        card.setCardinView(rootView, R.id.carddemo5);
//    }
//
//    /**
//     * Build venue card
//     */
//
//     private void initCard() {
//     // Create a Card
//     Card card = new Card(getActivity(), R.layout.crowd_card);
//     // Add card inner title
//     card.setTitle("Chill Bar in Down Town");
//
//     // Create a CardHeader (Title)
//     CardHeader header = new CardHeader(getActivity());
//     header.setTitle("Stephen's Green");
//     //Set to visible the expand/collapse button
//     header.setButtonExpandVisible(true);
//     // Add Header to card
//     card.addCardHeader(header);
//
//
//     // This provide a simple (and useless) expand area
//     //CardExpand expand = new CardExpand(getActivity());
//     CustomExpandCard expand = new CustomExpandCard(getActivity());
//     // Set inner title in Expand Area
//     //expand.setTitle(getString(R.string.city_1));
//     // Add expand to a card
//     card.addCardExpand(expand);
//
//
//     //card.setSwipeable(true);
//
//     // Create expandable area
//     CardThumbnail thumb = new CardThumbnail(getActivity());
//     thumb.setDrawableResource(R.drawable.stephensmtviewheader);
//     card.addCardThumbnail(thumb);
//
//     //Listeners
//     card.setOnClickListener(new Card.OnCardClickListener() {
//    @Override public void onClick(Card card, View view) {
//    //Toast.makeText(getActivity(), "Card was clicked", Toast.LENGTH_SHORT).show();
//    Intent intent = new Intent(getActivity(), LivePicsGalleryActivity.class);
//    startActivity(intent);
//    }
//    });
//
//
//     // Set card in the cardView
//     CardView cardView = (CardView) rootView.findViewById(R.id.carddemo);
//     ViewToClickToExpand viewToClickToExpand =
//     ViewToClickToExpand.builder()
//     .setupView(cardView).highlightView(false);
//     card.setViewToClickToExpand(viewToClickToExpand);
//     cardView.setCard(card);
//     }
//
//
//    /**
//     * Build Live CrowdCard
//     */
//
//     private void initLiveCrowdCard() {
//     liveCrowdRow card = new liveCrowdRow(getActivity());
//
//     card.setHeaderTitle("I did something");
//
//     CardViewNative liveCardView = (CardViewNative)getActivity().findViewById(R.id.live_crowdcard);
//     ViewToClickToExpand viewToClickToExpand =
//     ViewToClickToExpand.builder()
//     .setupView(liveCardView).highlightView(false);
//     card.setViewToClickToExpand(viewToClickToExpand);
//     liveCardView.setCard(card);
//     }
//
//
//    /**
//     * Build google play card
//     */
//
//     private void initCardGooglePlay() {
//     CustomCard card = new CustomCard(getActivity());
//
//     CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.demo_customcard);
//     ViewToClickToExpand viewToClickToExpand =
//     ViewToClickToExpand.builder()
//     .setupView(cardView).highlightView(false);
//     card.setViewToClickToExpand(viewToClickToExpand);
//     cardView.setCard(card);
//     }
//
//    /**
//     * Build google big card
//     */
//
//     private void initGplayBigCard() {
//     GplayBigCard card = new GplayBigCard(getActivity());
//     card.init();
//
//     CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.demo_customcard);
//     cardView.setCard(card);
//     }
//
//
//    /**
//     * A card as Google Play
//     */
//
//     public class GplayBigCard extends Card {
//
//     protected int resourceIdThumbnail = -1;
//
//     protected String headerTitle;
//
//     public GplayBigCard(Context context) {
//     super(context, R.layout.custom_card_inner_content);
//     }
//
//     public GplayBigCard(Context context, int innerLayout) {
//     super(context, innerLayout);
//     }
//
//     private void init() {
//
//     //Add header with the overflow button
//     CardHeader header = new CardHeader(getContext());
//     header.setButtonOverflowVisible(true);
//     header.setTitle(headerTitle);
//
//     header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
//    @Override public void onMenuItemClick(BaseCard card, MenuItem item) {
//    Toast.makeText(getContext(), "Item " + item.getTitle(), Toast.LENGTH_SHORT).show();
//    }
//    });
//
//     addCardHeader(header);
//
//     //Add thumbnail
//     GplayGridThumb thumbnail = new GplayGridThumb(getContext());
//     if (resourceIdThumbnail > -1)
//     thumbnail.setDrawableResource(resourceIdThumbnail);
//     else
//     thumbnail.setDrawableResource(R.drawable.ic_action_user_dark);
//     addCardThumbnail(thumbnail);
//
//     //Listeners
//     setOnClickListener(new OnCardClickListener() {
//    @Override public void onClick(Card card, View view) {
//    //Do something
//    }
//    });
//     }
//
//     @Override public void setupInnerViewElements(ViewGroup parent, View view) {
//
//     //Populate the inner elements
//
//     TextView title = (TextView) view.findViewById(R.id.custom_card_inner_green_description);
//     title.setText("FREE");
//
//     TextView subtitle = (TextView) view.findViewById(R.id.custom_card_main_inner_subtitle);
//     subtitle.setText("I did something");
//     //setText(secondaryTitle);
//
//     RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.custom_card_main_inner_ratingbar);
//
//     if (mRatingBar != null){
//     mRatingBar.setNumStars(5);
//     mRatingBar.setMax(5);
//     mRatingBar.setStepSize(0.5f);
//     mRatingBar.setRating(4.7f);
//     }
//     }
//
//
//     class GplayGridThumb extends CardThumbnail {
//
//     public GplayGridThumb(Context context) {
//     super(context);
//     }
//
//     @Override public void setupInnerViewElements(ViewGroup parent, View viewImage) {
//
//     viewImage.getLayoutParams().width = 196;
//     viewImage.getLayoutParams().height = 196;
//
//
//     }
//     }
//
//     }
//
//
//    /**
//     * Custom expand with picture
//     */
//
//    public class CustomExpandCard extends CardExpand {
//        public CustomExpandCard(Context context) {
//            super(context, R.layout.crowd_card_ratings_view);
//        }
//
//        @Override
//        public void setupInnerViewElements(ViewGroup parent, View view) {
//            if (view == null) return;
//            ImageView image = (ImageView) view.findViewById(R.id.livePic);
//
//            if (image != null) {
//                image.setImageResource(R.drawable.stephensgreenlive2);
//            }
//        }
//    }
//
//    // Need to add final } if wished to be readded to class

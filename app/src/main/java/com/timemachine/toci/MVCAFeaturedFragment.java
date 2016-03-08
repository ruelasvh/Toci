package com.timemachine.toci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by victorhugo on 5/24/15.
 */
public class MVCAFeaturedFragment extends Fragment {
    public MVCAFeaturedFragment() {
    }

    // Strings to call the webservice and root directory of pictures
    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowd.php";
    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/";
    String picUrl1 = "http://crowdzeeker.com/AppCrowdZeeker/AndroidFileUpload/uploads/IMG_20160224_142750.jpg";
    String picUrl2 = "http://www.mollysmtview.com/images/img_1680.jpg";
    String picUrl3 = "http://www.mollysmtview.com/images/img_1680.jpg";



    private View rootView;
    private ListView listView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getActivity().setContentView(R.layout.fragment_mv_ca_featured);


        //new HttpAsyncTask().execute(sortScript);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mv_ca_featured, container, false);

        liveCrowdRow crowds[] = new liveCrowdRow[] {
                new liveCrowdRow(sortScript,"Crowd_1", "Subtitle_1", "Distance_1"),
                new liveCrowdRow(sortScript,"Crowd_2", "Subtitle_2", "Distance_2"),
                new liveCrowdRow(sortScript,"Crowd_3", "Subtitle_3", "Distance_3"),
                new liveCrowdRow(sortScript,"Crowd_4", "Subtitle_4", "Distance_4"),
                new liveCrowdRow(sortScript,"Crowd_5", "Subtitle_5", "Distance_5"),
        };

        liveCrowdRowAdapter adapter = new liveCrowdRowAdapter(getActivity(),
                R.layout.row, crowds);

        listView1 = (ListView) rootView.findViewById(R.id.crowds_listview);
        listView1.setAdapter(adapter);

//        ListView listView = (ListView) rootView.findViewById(R.id.crowds_listview);
//        ArrayList<liveCrowdRow> list = new ArrayList<>();
//
//
//        liveCrowdRow row1 = new liveCrowdRow();
//        liveCrowdRow row2 = new liveCrowdRow();
//        list.add(row1);
//        list.add(row2);


//        CrowdArrayAdapter adapter = new CrowdArrayAdapter(this.getActivity(), list);
//        listView.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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

    /**
     * Build venue card


     private void initCard() {
     // Create a Card
     Card card = new Card(getActivity(), R.layout.crowd_card);
     // Add card inner title
     card.setTitle("Chill Bar in Down Town");

     // Create a CardHeader (Title)
     CardHeader header = new CardHeader(getActivity());
     header.setTitle("Stephen's Green");
     //Set to visible the expand/collapse button
     header.setButtonExpandVisible(true);
     // Add Header to card
     card.addCardHeader(header);


     // This provide a simple (and useless) expand area
     //CardExpand expand = new CardExpand(getActivity());
     CustomExpandCard expand = new CustomExpandCard(getActivity());
     // Set inner title in Expand Area
     //expand.setTitle(getString(R.string.city_1));
     // Add expand to a card
     card.addCardExpand(expand);


     //card.setSwipeable(true);

     // Create expandable area
     CardThumbnail thumb = new CardThumbnail(getActivity());
     thumb.setDrawableResource(R.drawable.stephensmtviewheader);
     card.addCardThumbnail(thumb);

     //Listeners
     card.setOnClickListener(new Card.OnCardClickListener() {
    @Override public void onClick(Card card, View view) {
    //Toast.makeText(getActivity(), "Card was clicked", Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(getActivity(), LivePicsGalleryActivity.class);
    startActivity(intent);
    }
    });


     // Set card in the cardView
     CardView cardView = (CardView) rootView.findViewById(R.id.carddemo);
     ViewToClickToExpand viewToClickToExpand =
     ViewToClickToExpand.builder()
     .setupView(cardView).highlightView(false);
     card.setViewToClickToExpand(viewToClickToExpand);
     cardView.setCard(card);
     }
     */

    /**
     * Build Live CrowdCard

     private void initLiveCrowdCard() {
     liveCrowdRow card = new liveCrowdRow(getActivity());

     card.setHeaderTitle("I did something");

     CardViewNative liveCardView = (CardViewNative)getActivity().findViewById(R.id.live_crowdcard);
     ViewToClickToExpand viewToClickToExpand =
     ViewToClickToExpand.builder()
     .setupView(liveCardView).highlightView(false);
     card.setViewToClickToExpand(viewToClickToExpand);
     liveCardView.setCard(card);
     }
     */

    /**
     * Build google play card

     private void initCardGooglePlay() {
     CustomCard card = new CustomCard(getActivity());

     CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.demo_customcard);
     ViewToClickToExpand viewToClickToExpand =
     ViewToClickToExpand.builder()
     .setupView(cardView).highlightView(false);
     card.setViewToClickToExpand(viewToClickToExpand);
     cardView.setCard(card);
     }
     */

    /**
     * Build google big card

     private void initGplayBigCard() {
     GplayBigCard card = new GplayBigCard(getActivity());
     card.init();

     CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.demo_customcard);
     cardView.setCard(card);
     }
     */

    /**
     * A card as Google Play

     public class GplayBigCard extends Card {

     protected int resourceIdThumbnail = -1;

     protected String headerTitle;

     public GplayBigCard(Context context) {
     super(context, R.layout.custom_card_inner_content);
     }

     public GplayBigCard(Context context, int innerLayout) {
     super(context, innerLayout);
     }

     private void init() {

     //Add header with the overflow button
     CardHeader header = new CardHeader(getContext());
     header.setButtonOverflowVisible(true);
     header.setTitle(headerTitle);

     header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
    @Override public void onMenuItemClick(BaseCard card, MenuItem item) {
    Toast.makeText(getContext(), "Item " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }
    });

     addCardHeader(header);

     //Add thumbnail
     GplayGridThumb thumbnail = new GplayGridThumb(getContext());
     if (resourceIdThumbnail > -1)
     thumbnail.setDrawableResource(resourceIdThumbnail);
     else
     thumbnail.setDrawableResource(R.drawable.ic_action_user_dark);
     addCardThumbnail(thumbnail);

     //Listeners
     setOnClickListener(new OnCardClickListener() {
    @Override public void onClick(Card card, View view) {
    //Do something
    }
    });
     }

     @Override public void setupInnerViewElements(ViewGroup parent, View view) {

     //Populate the inner elements

     TextView title = (TextView) view.findViewById(R.id.custom_card_inner_green_description);
     title.setText("FREE");

     TextView subtitle = (TextView) view.findViewById(R.id.custom_card_main_inner_subtitle);
     subtitle.setText("I did something");
     //setText(secondaryTitle);

     RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.custom_card_main_inner_ratingbar);

     if (mRatingBar != null){
     mRatingBar.setNumStars(5);
     mRatingBar.setMax(5);
     mRatingBar.setStepSize(0.5f);
     mRatingBar.setRating(4.7f);
     }
     }


     class GplayGridThumb extends CardThumbnail {

     public GplayGridThumb(Context context) {
     super(context);
     }

     @Override public void setupInnerViewElements(ViewGroup parent, View viewImage) {

     viewImage.getLayoutParams().width = 196;
     viewImage.getLayoutParams().height = 196;


     }
     }

     }
     */

    /**
     * Custom expand with picture


    public class CustomExpandCard extends CardExpand {
        public CustomExpandCard(Context context) {
            super(context, R.layout.crowd_card_ratings_view);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            if (view == null) return;
            ImageView image = (ImageView) view.findViewById(R.id.livePic);

            if (image != null) {
                image.setImageResource(R.drawable.stephensgreenlive2);
            }
        }
    }

     */



    public class CrowdArrayAdapter extends ArrayAdapter<FrameLayout> {

        LayoutInflater inflater;

        public CrowdArrayAdapter(Context context, ArrayList<FrameLayout> list) {
            super(context, R.layout.row, list);

            inflater = getActivity().getLayoutInflater();
            //inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            liveCrowdRowViewHolder holder = null;

            if (convertView == null) {
                // inflate if not recycled
                convertView = inflater.inflate(R.layout.row, container, false);

                holder = new liveCrowdRowViewHolder(convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (liveCrowdRowViewHolder) convertView.getTag();
            }

//            Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/img_1694.jpg").into(viewHolder.livepic);

            return convertView;
        }
    }


    public static String GET(String url) {
        InputStream inputStream;
        String result = "";

        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            new DisplayImageFromURL((ImageView) getActivity().findViewById(R.id.livepic))
                    .execute(imageBaseDirectory + result);
        }
    }

    private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        // constructor
        public DisplayImageFromURL(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

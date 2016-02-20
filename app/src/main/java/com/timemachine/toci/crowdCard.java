package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by victorhugo on 5/26/15.
 */
public class crowdCard extends Card {

    private String Crowd_Subtitle;
    private String Crowd_RatingComment;
    private String Crowd_CoverCharge;
    private float Crowd_Rating;
    private String Crowd_Specials_Header;
    private int Crowd_Specials_Header_color;
    private String Crowd_Specials_text1;
    private int Crowd_Specials_text1_color;
    private String Crowd_Specials_text2;
    private int Crowd_Specials_text2_color;


    /*
    public crowdCard(Context context) {
        super(context, R.layout.crowd_card);
        //init();
    }
    */

    public crowdCard(Context context, int innerLayout) {
        super(context, innerLayout);
        // can call function to initialize here like init();
    }

    public void setCrowdLogo(int logo) {
        LocalThumbnail thumbnail = new LocalThumbnail(getContext());
        thumbnail.setDrawableResource(logo);
        addCardThumbnail(thumbnail);
    }

    public void setCrowdLogoUrl(String myUrl) {
        // Add Thumbnail
        UrlThumbnail thumbnail = new UrlThumbnail(getContext());
        // Set true to use external library
        thumbnail.setExternalUsage(true);
        // Set the url
        thumbnail.setUrl(myUrl);
        // Add thumbnail to card
        addCardThumbnail(thumbnail);
    }

    public void setCrowdPicUrl(String webService) {

        new HttpAsyncTask().execute(webService);
    }

    public void setCrowdTitle(String cTitle) {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(cTitle);
        header.setButtonExpandVisible(true);
        addCardHeader(header);
    }

    public void setCrowdTitle(String cTitle, Boolean isExpandable) {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(cTitle);
        header.setButtonExpandVisible(isExpandable);
        addCardHeader(header);
    }

    public void setCrowdSubtitle(String mSubtitle) {
        this.Crowd_Subtitle = mSubtitle;
    }

    public void setCrowdRatingComment(String mRatingComment) {
        this.Crowd_RatingComment = mRatingComment;
    }

    public void setCrowdCoverCharge(String mCoverCharge) {
        this.Crowd_CoverCharge = mCoverCharge;
    }

    public void setCrowdRating(float mRating) {
        this.Crowd_Rating = mRating;
    }

    public void setSpecialsHeader(String mSpecialsHeader, int mSpecialHeaderColor) {
        this.Crowd_Specials_Header = mSpecialsHeader;
        this.Crowd_Specials_Header_color = mSpecialHeaderColor;
    }

    public void setSpecials1(String mSpecials1, int mSpecials1Color) {
        this.Crowd_Specials_text1 = mSpecials1;
        this.Crowd_Specials_text1_color = mSpecials1Color;
    }

    public void setSpecials2(String mSpecials2, int mSpecials2Color) {
        this.Crowd_Specials_text2 = mSpecials2;
        this.Crowd_Specials_text2_color = mSpecials2Color;
    }

    public void setCrowdExpand(int layout, int view, int img) {
        crowdCardExpand expand = new crowdCardExpand(getContext(), layout);
        expand.Set_Img(view, img);
        addCardExpand(expand);
    }

    public void setCrowdMapExpand(int layout) {
        CardExpand expand = new CardExpand(getContext(), layout);
        addCardExpand(expand);
    }


    public void setCrowdLivePics(final Class livePicsClass) {
        setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getContext(), livePicsClass);
                getContext().startActivity(intent);
            }
        });
    }

    public void setCardinView(View parentView, int childView) {
        CardView cardView = (CardView) parentView.findViewById(childView);
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .setupView(cardView).highlightView(false);
        setViewToClickToExpand(viewToClickToExpand);
        cardView.setCard(this);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        TextView subTitle = (TextView) view.findViewById(R.id.crowd_Card_inner_simple_title);
        subTitle.setText(Crowd_Subtitle);

        TextView ratingComment = (TextView) view.findViewById(R.id.crowd_Card_distance);
        ratingComment.setText(Crowd_RatingComment);

        TextView coverPrice = (TextView) view.findViewById(R.id.crowd_Card_inner_cover_charge);
        coverPrice.setText(Crowd_CoverCharge);

        RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.crowd_Card_inner_ratingbar);

        mRatingBar.setNumStars(5);
        mRatingBar.setMax(5);
        mRatingBar.setStepSize(0.5f);
        mRatingBar.setRating(Crowd_Rating);

        TextView specialsHeader = (TextView) view.findViewById(R.id.crowd_Card_specials_header);
        specialsHeader.setText(Crowd_Specials_Header);
        specialsHeader.setTextColor(Crowd_Specials_Header_color);

        TextView specialsText1 = (TextView) view.findViewById(R.id.crowd_Card_special1);
        specialsText1.setText(Crowd_Specials_text1);
        specialsText1.setTextColor(Crowd_Specials_text1_color);

        TextView specialsText2 = (TextView) view.findViewById(R.id.crowd_Card_special2);
        specialsText2.setText(Crowd_Specials_text2);
        specialsText2.setTextColor(Crowd_Specials_text2_color);
    }

    class LocalThumbnail extends CardThumbnail {

        public LocalThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {



            if (viewImage != null) {
                if (parent != null && parent.getResources() != null) {
                    DisplayMetrics metrics = parent.getResources().getDisplayMetrics();

                    int base = 198;

                    if (metrics != null) {
                        viewImage.getLayoutParams().width = (int) (base * metrics.density);
                        viewImage.getLayoutParams().height = (int) (base * metrics.density);
                    } else {
                        viewImage.getLayoutParams().width = 196;
                        viewImage.getLayoutParams().height = 196;
                    }
                }
            }
            /**
            viewImage.getLayoutParams().width = 156;
            viewImage.getLayoutParams().height = 156;
            */
        }
    }

    class UrlThumbnail extends CardThumbnail {

        // Variables to use in methods for passing url and ImageView
        private String myUrl = "";

        // Method to get Url
        public void setUrl(String url) {
            myUrl = url;
        }


        public UrlThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {

            Picasso.with(getContext())
                    .load(myUrl)
                    //.placeholder(R.drawable.crowdzeeker_logo)
                    .error(R.drawable.crowdzeeker_logo)
                    .resizeDimen(R.dimen.list_detail_image_size_high_res, R.dimen.list_detail_image_size_high_res)
                    .centerInside()
                    .into((ImageView) viewImage);


            if (viewImage != null) {
                if (parent != null && parent.getResources() != null) {
                    DisplayMetrics metrics = parent.getResources().getDisplayMetrics();

                    int base = 198;

                    if (metrics != null) {
                        viewImage.getLayoutParams().width = (int) (base * metrics.density);
                        viewImage.getLayoutParams().height = (int) (base * metrics.density);
                    } else {
                        viewImage.getLayoutParams().width = 396;
                        viewImage.getLayoutParams().height = 396;
                    }
                }
            }

            /*
            viewImage.getLayoutParams().width = 350;
            viewImage.getLayoutParams().height = 370;
            */
        }
    }

    /*
     * Helper function to convert php stream result to string
     */

    public static String GET(String url) {
        InputStream inputStream;
        String result = "";

        try {
            // create HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputStream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "GET method to get php response was not successful";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputStream to String
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

        // onPostExecute displays the results of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}

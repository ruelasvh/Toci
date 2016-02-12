package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    private static String Crowd_Subtitle = "";
    private static String Crowd_RatingComment = "";
    private static String Crowd_CoverCharge = "";
    private static float Crowd_Rating = 0f;
    private static String Crowd_Specials_Header = "";
    private static int Crowd_Specials_Header_color;
    private static String Crowd_Specials_text1 = "";
    private static int Crowd_Specials_text1_color;
    private static String Crowd_Specials_text2 = "";
    private static int Crowd_Specials_text2_color;


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

    public void setCrowdTitle(String cTitle) {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(cTitle);
        header.setButtonExpandVisible(true);
        addCardHeader(header);
    }

    public void setCrowdSubtitle(String mSubtitle) {
        Crowd_Subtitle = mSubtitle;
    }

    public void setCrowdRatingComment(String mRatingComment) {
        Crowd_RatingComment = mRatingComment;
    }

    public void setCrowdCoverCharge(String mCoverCharge) {
        Crowd_CoverCharge = mCoverCharge;
    }

    public void setCrowdRating(float mRating) {
        Crowd_Rating = mRating;
    }

    public void setSpecialsHeader(String mSpecialsHeader, int mSpecialHeaderColor) {
        Crowd_Specials_Header = mSpecialsHeader;
        Crowd_Specials_Header_color = mSpecialHeaderColor;
    }

    public void setSpecials1(String mSpecials1, int mSpecials1Color) {
        Crowd_Specials_text1 = mSpecials1;
        Crowd_Specials_text1_color = mSpecials1Color;
    }

    public void setSpecials2(String mSpecials2, int mSpecials2Color) {
        Crowd_Specials_text2 = mSpecials2;
        Crowd_Specials_text2_color = mSpecials2Color;
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
                    .placeholder(R.drawable.crowdzeeker_logo)
                    .error(R.drawable.thumbsdown)
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
}

package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
        CustomCardThumbnail thumbnail = new CustomCardThumbnail(getContext());
        thumbnail.setDrawableResource(logo);
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

        TextView ratingComment = (TextView) view.findViewById(R.id.crowd_Card_inner_rating_comment);
        ratingComment.setText(Crowd_RatingComment);

        TextView coverPrice = (TextView) view.findViewById(R.id.crowd_Card_inner_cover_charge);
        coverPrice.setText(Crowd_CoverCharge);

        RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.crowd_Card_inner_ratingbar);

        mRatingBar.setNumStars(5);
        mRatingBar.setMax(5);
        mRatingBar.setStepSize(0.5f);
        mRatingBar.setRating(Crowd_Rating);
    }

    class CustomCardThumbnail extends CardThumbnail {

        public CustomCardThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {


            viewImage.getLayoutParams().width = 156;
            viewImage.getLayoutParams().height = 156;

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

        }
    }

}

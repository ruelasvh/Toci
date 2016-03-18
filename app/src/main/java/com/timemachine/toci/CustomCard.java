package com.timemachine.toci;

/**
 * Created by victorhugo on 5/26/15.
 */


import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

/**
 * This class provides a simple example of CrowdCard.
 */
public class CustomCard extends Card {

    public CustomCard(Context context) {
        super(context, R.layout.custom_card_inner_content);
        init();
    }

    public CustomCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }


    private void init() {

        // Set up header properties
        CardHeader header = new CardHeader(getContext());
        header.setButtonOverflowVisible(true);
        header.setTitle("Molly Magee's");
        header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        // Add header properties
        addCardHeader(header);

        // Add thumbnail
        CustomCardThumbnail thumbnail = new CustomCardThumbnail(getContext());
        thumbnail.setDrawableResource(R.drawable.mollysmtviewheader);
        addCardThumbnail(thumbnail);

        // Add expand properties
        CustomExpandCard expand = new CustomExpandCard(getContext());
        addCardExpand(expand);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        TextView title = (TextView) view.findViewById(R.id.custom_card_main_inner_subtitle);
        title.setText("Famous Irish Pub");

        TextView subtitle = (TextView) view.findViewById(R.id.custom_card_inner_green_description);
        subtitle.setText("FREE");

        RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.custom_card_main_inner_ratingbar);

        mRatingBar.setNumStars(5);
        mRatingBar.setMax(5);
        mRatingBar.setStepSize(0.5f);
        mRatingBar.setRating(4.7f);
    }

    class CustomCardThumbnail extends CardThumbnail {

        public CustomCardThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {


            viewImage.getLayoutParams().width = 156;
            viewImage.getLayoutParams().height = 156;
            /*
            if (viewImage != null) {
                if (parent!=null && parent.getResources()!=null){
                    DisplayMetrics metrics=parent.getResources().getDisplayMetrics();

                    int base = 98;

                    if (metrics!=null){
                        viewImage.getLayoutParams().width = (int)(base*metrics.density);
                        viewImage.getLayoutParams().height = (int)(base*metrics.density);
                    }else{
                        viewImage.getLayoutParams().width = 156;
                        viewImage.getLayoutParams().height = 156;
                    }
                }
            }
        */
        }
    }

    /**
     * Custom expand with picture
     */

    public class CustomExpandCard extends CardExpand {
        public CustomExpandCard(Context context) {
            super(context, R.layout.crowd_card_ratings_view);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            if (view == null) return;
            ImageView image = (ImageView) view.findViewById(R.id.livePic);

            if (image != null) {
                image.setImageResource(R.drawable.mollysmtviewlive);
            }
        }
    }
}

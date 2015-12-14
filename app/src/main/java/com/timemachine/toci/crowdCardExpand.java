package com.timemachine.toci;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by victorhugo on 7/8/15.
 */
public class crowdCardExpand extends CardExpand {

    private static int mView;
    private static int mImage;

    public void Set_Img(int view, int img) {
        mView = view;
        mImage = img;
    }

    public crowdCardExpand(Context context, int mLayout) {
        super(context, mLayout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view == null) return;
        ImageView image = (ImageView) view.findViewById(mView);

        if (image != null) {
            image.setImageDrawable(new NavigationDrawerFragment.RoundImage(BitmapFactory.decodeResource(getContext().getResources(), mImage)));
        }

        TextView username = (TextView) view.findViewById(R.id.userName);
        username.setText("CrowdZeeker#1");
        /*
        RatingBar userrating = (RatingBar) parent.findViewById(R.id.userRating);

        userrating.setNumStars(5);
        userrating.setMax(5);
        userrating.setStepSize(0.5f);
        userrating.setRating(4.0f);
        */
        TextView usercomment = (TextView) view.findViewById(R.id.userComment);
        usercomment.setText("I've been to this restaurant with friends and I've had a blast. The sliders and their sangria is " +
                "amazing. I have also been here with family and it's a great atmosphere. I love this place!! Highly recommend it!");
    }
}

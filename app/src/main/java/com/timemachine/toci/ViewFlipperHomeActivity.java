package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

/**
 * Created by victorhugo on 4/19/15.
 */
public class ViewFlipperHomeActivity extends Activity {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private ViewFlipper mViewFlipper;
    private Context mContext;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    private Button getStartedBtn;
    private Button nextViewBtn1, nextViewBtn2, nextViewBtn3, nextViewBtn4, nextViewBtn5;
    private ImageButton logoBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
        mViewFlipper.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        detector.onTouchEvent(event);
                        return true;
                    }
                });
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(4000);
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
        mViewFlipper.startFlipping();

        getStartedBtn = (Button) findViewById(R.id.getStartedBtn);
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStarted();
            }
        });

        logoBtn = (ImageButton) findViewById(R.id.logoBtn);
        logoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStarted();
            }
        });

        nextViewBtn1 = (Button) findViewById(R.id.btnView1);
        nextViewBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.stopFlipping();
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(6000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.startFlipping();
            }
        });

        nextViewBtn2 = (Button) findViewById(R.id.btnView2);
        nextViewBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.stopFlipping();
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(6000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.startFlipping();
            }
        });

        nextViewBtn3 = (Button) findViewById(R.id.btnView3);
        nextViewBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.stopFlipping();
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(6000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.startFlipping();
            }
        });

        nextViewBtn4 = (Button) findViewById(R.id.btnView4);
        nextViewBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.stopFlipping();
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(6000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.startFlipping();
            }
        });

        nextViewBtn5 = (Button) findViewById(R.id.btnView5);
        nextViewBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.stopFlipping();
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(6000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.startFlipping();
            }
        });

    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.stopFlipping();
                    mViewFlipper.setAutoStart(true);
                    mViewFlipper.setFlipInterval(6000);
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                    mViewFlipper.showNext();
                    mViewFlipper.startFlipping();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.stopFlipping();
                    mViewFlipper.setAutoStart(true);
                    mViewFlipper.setFlipInterval(6000);
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
                    mViewFlipper.showPrevious();
                    mViewFlipper.startFlipping();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public void getStarted() {
        Intent intent = new Intent(this, HomeMaterialActivity.class);
        startActivity(intent);
    }

    public void startSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}

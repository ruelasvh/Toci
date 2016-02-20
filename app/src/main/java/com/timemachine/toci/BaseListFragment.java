package com.timemachine.toci;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

/**
 * Created by victorhugo on 2/17/16.
 * Copyright (c) 2016 CrowdZeeker, LLC All rights reserved.
 */

/*
 * Abstract class to extend when creating lists of crowdCards.
 *
 * Fragment based from cardslib Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 */
public abstract class BaseListFragment extends Fragment {

    protected boolean mListShown;
    protected View mProgressContainer;
    protected View mListContainer;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListFragment(view);
    }

    /**
     * Setup the list fragment
     *
     * @param root
     */
    protected void setupListFragment(View root) {

        mListContainer = root.findViewById(R.id.crowdCard_listContainer);
        mProgressContainer = root.findViewById(R.id.crowdCard_progressContainer);
        mListShown = true;
    }

    protected void displayList() {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    protected void hideList(boolean animate){
        setListShown(false, animate);
    }

    /**
     * @param shown
     * @param animate
     */
    protected void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown)
            return;
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

}

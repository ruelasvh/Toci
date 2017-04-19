package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Victor Ruelas on 4/16/17.
 * Copyright (c) 2017 CrowdZeeker, LLC. All rights reserved.
 */

public class SwipeableLiveCrowdListAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    private List<LiveCrowd> crowdsList;
    AppPrefs mAppPrefs;
    ImageButton searchButton;
    TextView searchText;
    private ShowCrowdsFragment.OnFragmentSelectedListener mListener;

    public SwipeableLiveCrowdListAdapter(Context context, int layoutResourceId, LiveCrowd[] crowds) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.crowdsList = new ArrayList<>(Arrays.asList(crowds));
        this.mAppPrefs = new AppPrefs(context);
        this.searchButton = new ImageButton(context);
        this.searchText = new TextView(context);
        this.mListener = (ShowCrowdsFragment.OnFragmentSelectedListener) context;

        if (this.crowdsList.isEmpty()) showSearchCrowds(true);
    }

    @Override
    public int getCount() {
        return crowdsList.size();
    }

    @Override
    public Object getItem(int position) {
        return crowdsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(layoutResourceId, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Picasso.with(context).cancelRequest(viewHolder.livepic);
        }

        final LiveCrowd crowd = crowdsList.get(position);
        viewHolder.title.setText(crowd.getTitle());
        viewHolder.timeago.setText(crowd.getTimeago());
        viewHolder.distance.setText(Float.toString(crowd.getDistance()) + " mi");
        viewHolder.livepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LivePicsGalleryActivity.class);
                intent.putExtra("crowd", SerializeLiveCrowd.toJson(crowd));
                context.startActivity(intent);
            }
        });

        Picasso.with(context).load( crowd.getPicUrls().get( crowd.getPicUrls().size() - 1).get(0) )
                .into( viewHolder.livepic);

        final CardView cardView = viewHolder.cardView;
        final CoordinatorLayout coordinatorLayout = viewHolder.coordinatorLayout;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) cardView.getLayoutParams();
        final SwipeDismissBehavior<CardView> behavior = new SwipeDismissBehavior<>();
        behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_END_TO_START);
        behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {

            @Override
            public void onDismiss(final View view) {

                crowdsList.remove(position);
                mAppPrefs.removeFavorite_crowd(crowd.getId());
                notifyDataSetChanged();

                if (crowdsList.isEmpty()) {
                    showSearchCrowds(true);
                }

                Snackbar.make(((Activity) context).findViewById(R.id.crowds_listview),
                        "Removed " + crowd.getTitle(), Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSearchCrowds(false);
                                crowdsList.add(position, crowd);
                                mAppPrefs.setFavorite_crowd(crowd.getId());
                                notifyDataSetChanged();
                            }
                        })
                        .show();
            }

            @Override
            public void onDragStateChanged(int i) {
            }
        });
        params.setBehavior(behavior);

        coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return behavior.onTouchEvent(coordinatorLayout, cardView, event);
            }
        });

        return convertView;
    }

    class ViewHolder {

        CoordinatorLayout coordinatorLayout;
        CardView cardView;
        ImageButton livepic;
        TextView title;
        TextView timeago;
        TextView distance;

        public ViewHolder(View convertView) {
            coordinatorLayout = (CoordinatorLayout) convertView.findViewById(R.id.coordinatorLayout);
            cardView = (CardView) convertView.findViewById(R.id.cardView);
            livepic = (ImageButton) convertView.findViewById(R.id.livepic);
            title = (TextView) convertView.findViewById(R.id.title);
            timeago = (TextView) convertView.findViewById(R.id.timeago);
            distance = (TextView) convertView.findViewById(R.id.distance);
        }
    }

    public void updateList(LiveCrowd[] crowds) {
        this.crowdsList = new ArrayList<>(Arrays.asList(crowds));
    }

    private void showSearchCrowds(boolean show) {

        RelativeLayout rootLayout = (RelativeLayout)((Activity) context).findViewById(R.id.root);
        rootLayout.removeView(searchButton);
        rootLayout.removeView(searchText);

        if (show) {
            searchButton.setVisibility(View.VISIBLE);
            searchButton.setId('1');
            searchButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            searchButton.setImageResource(R.drawable.ic_search_white_48dp);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Launch SearchFragment and select it in NavDrawer
                    int searchFragPosition = 0;
                    mListener.onNavDrawerItemSelected(searchFragPosition);

                }
            });

            searchText.setVisibility(View.VISIBLE);
            searchText.setId('0');
            searchText.setText("Search");
            searchText.setTextColor(context.getResources().getColor(R.color.white));
            searchText.setTextSize(24.f);
//            searchText.setTypeface(null, Typeface.BOLD);

            rootLayout.setGravity(Gravity.CENTER);
            rootLayout.addView(searchButton);
            rootLayout.addView(searchText);

            RelativeLayout.LayoutParams layoutParamsSearchButton = (RelativeLayout.LayoutParams) searchButton.getLayoutParams();

            layoutParamsSearchButton.addRule(RelativeLayout.ABOVE, searchText.getId());
            layoutParamsSearchButton.addRule(RelativeLayout.CENTER_IN_PARENT);
            searchButton.setLayoutParams(layoutParamsSearchButton);

            RelativeLayout.LayoutParams layoutParamsSearchText = (RelativeLayout.LayoutParams) searchText.getLayoutParams();
            layoutParamsSearchText.addRule(RelativeLayout.CENTER_IN_PARENT);
            searchText.setLayoutParams(layoutParamsSearchText);
        } else {
            Log.d(this.getClass().getSimpleName(), "WENT INTO HIDDING BUTTONS");
            searchButton.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
        }
    }

}

package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Victor Ruelas on 3/7/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class LiveCrowdListAdapter extends ArrayAdapter<LiveCrowd> {

    /**
     * Logcat tag
     */
    private static final String TAG = LiveCrowdListAdapter.class.getSimpleName();

    Context context;
    int layoutResourceId;
    List<LiveCrowd> crowds;


    public LiveCrowdListAdapter(Context context, int layoutResourceId, LiveCrowd[] crowds) {
        super(context, layoutResourceId, crowds);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.crowds = new ArrayList<>(Arrays.asList(crowds));

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final ViewHolder viewHolder;

        if (rowView == null) {
            rowView = ((Activity)context).getLayoutInflater().inflate(layoutResourceId, parent, false);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) rowView.getTag();
            Picasso.with(context).cancelRequest(viewHolder.livepic);
        }

        final LiveCrowd crowd = crowds.get(position);
        viewHolder.title.setText(crowd.getTitle());
        viewHolder.timeago.setText(crowd.getTimeago());
        viewHolder.distance.setText(crowd.getDistance());
        viewHolder.livepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, crowdRow.getDetailedCrowd());
                Intent intent = new Intent(context, LivePicsGalleryActivity.class);
                intent.putExtra("crowd", SerializeLiveCrowd.toJson(crowd));
                context.startActivity(intent);
            }
        });

        Picasso.with(context).load( crowd.getPicUrls().get( crowd.getPicUrls().size() - 1).get(0) )
                .into( viewHolder.livepic);

        return rowView;
    }

    /**
     * Holder which prevents UI to find objects every time list is loaded
     */
    static class ViewHolder {

        ImageButton livepic;
        TextView title;
        TextView timeago;
        TextView distance;

        public ViewHolder(View convertView) {
            livepic = (ImageButton) convertView.findViewById(R.id.livepic);
            title = (TextView) convertView.findViewById(R.id.title);
            timeago = (TextView) convertView.findViewById(R.id.timeago);
            distance = (TextView) convertView.findViewById(R.id.distance);
        }
    }

    public void updateList(LiveCrowd[] crowds) {
        this.crowds = new ArrayList<>(Arrays.asList(crowds));
    }
}

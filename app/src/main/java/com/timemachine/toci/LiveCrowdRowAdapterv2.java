package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

/**
 * Created by Victor Ruelas on 3/7/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class LiveCrowdRowAdapterv2 extends ArrayAdapter<LiveCrowdRow> {

    /**
     * Logcat tag
     */
    private static final String TAG = LiveCrowdRowAdapterv2.class.getSimpleName();

    int FADE_IN_DURATION = 500;

    Context context;
    int layoutResourceId;
    LiveCrowdRow[] crowds;


    public LiveCrowdRowAdapterv2(Context context, int layoutResourceId, LiveCrowdRow[] crowds) {
        super(context, layoutResourceId, crowds);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.crowds = crowds;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        final liveCrowdRowHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new liveCrowdRowHolder();
            holder.livepic = (ImageButton) row.findViewById(R.id.livepic);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.timeago = (TextView) row.findViewById(R.id.timeago);
            holder.distance = (TextView) row.findViewById(R.id.distance);

            row.setTag(holder);
        }
        else {
            holder = (liveCrowdRowHolder) row.getTag();
            Picasso.with(context).cancelRequest(holder.livepic);
        }

        final LiveCrowdRow crowdRow = crowds[position];
        holder.title.setText(crowdRow.getTitle());
        holder.timeago.setText(crowdRow.getTimeago());
        holder.distance.setText(crowdRow.getDistance());
        holder.livepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, crowdRow.getDetailedCrowd());
                Intent intent = new Intent(context, LivePicsGalleryActivity.class);
                intent.putExtra("id", crowdRow.getId());
                intent.putExtra("name", crowdRow.getTitle());
                intent.putExtra("city", crowdRow.getCity());
                intent.putExtra("timeAgo", crowdRow.getTimeago());
                intent.putExtra("picUrls", crowdRow.getPicUrls());
                context.startActivity(intent);
            }
        });

        Picasso.with(context).load( crowdRow.getPicUrls().get( crowdRow.getPicUrls().size() - 1).get(0) )
                .into( holder.livepic);

        return row;
    }

    /**
     * Holder which prevents UI to find objects every time list is loaded
     */
    static class liveCrowdRowHolder {

        ImageButton livepic;
        TextView title;
        TextView timeago;
        TextView distance;

    }


    /**
     * Taks {@link AsyncTask} to put image into view.
     * @return bitmap
     */
    private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;


        // constructor
        public DisplayImageFromURL(ImageView bmImage) {
            this.bmImage = bmImage;
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
            if (isCancelled()) {
                result = null;
            }
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(context.getResources(), result)
                    });
//            // Show this background while loading bitmap
//            bmImage.setImageDrawable(
//                    new BitmapDrawable(context.getResources(), mLoadingBitmap)
//            );
            bmImage.setImageDrawable(td);
            td.startTransition(FADE_IN_DURATION);
            // Delete rest of method body except for next line
            // to remove fade in animation.
            //bmImage.setImageBitmap(result);
        }
    }

}

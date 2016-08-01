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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Victor Ruelas on 3/7/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class liveCrowdRowAdapterv2 extends ArrayAdapter<liveCrowdRow> {

    /**
     * Logcat tag
     */
    private static final String TAG = liveCrowdRowAdapterv2.class.getSimpleName();

    int FADE_IN_DURATION = 500;

    Context context;
    int layoutResourceId;
    liveCrowdRow[] crowds;


    public liveCrowdRowAdapterv2(Context context, int layoutResourceId, liveCrowdRow[] crowds) {
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

        final liveCrowdRow crowdRow = crowds[position];
        holder.title.setText(crowdRow.title);
        holder.timeago.setText(crowdRow.timeago);
        holder.distance.setText(crowdRow.distance);
        holder.livepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, crowdRow.detailedCrowd);
                intent.putExtra("id", crowdRow.id);
                intent.putExtra("name", crowdRow.title);
                intent.putExtra("city", crowdRow.city);
                intent.putExtra("timeAgo", crowdRow.timeago);
                intent.putExtra("picUrls", crowdRow.picUrls);
                context.startActivity(intent);
            }
        });

        Picasso.with(context).load( crowdRow.picUrls.get( crowdRow.picUrls.size() - 1).get(0) )
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

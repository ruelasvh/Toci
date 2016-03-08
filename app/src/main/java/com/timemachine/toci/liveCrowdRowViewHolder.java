package com.timemachine.toci;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Victor Ruelas on 3/7/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class liveCrowdRowViewHolder {

    private View row;

    private ImageView livepic;
    private TextView title = null;
    private TextView subtitle = null;
    private TextView distance = null;
    int position;

    public liveCrowdRowViewHolder(View row) {
        this.row = row;
    }

    public TextView getTitle() {
        if (this.title == null) {
            this.title = (TextView) row.findViewById(R.id.title);
        }
        return this.title;
    }

    public TextView getSubtitle() {
        if (this.subtitle == null) {
            this.subtitle = (TextView) row.findViewById(R.id.subtitle);
        }
        return this.subtitle;
    }

    public TextView getDistance() {
        if (this.distance == null) {
            this.distance = (TextView) row.findViewById(R.id.distance);
        }
        return this.distance;
    }

    public void setTitle() {
        if (this.title == null) {
            this.title.setText("Title");
        }
    }

}

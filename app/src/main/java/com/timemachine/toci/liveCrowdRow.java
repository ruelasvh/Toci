package com.timemachine.toci;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor Ruelas on 3/7/16.
 */
public class liveCrowdRow {

    public String id;
    public String title;
    public String city;
    public String timeago;
    public String distance;
    public HashMap<Integer, ArrayList<String>> picUrls;
    public Class detailedCrowd;

    public liveCrowdRow(String id, String title, String city, String timeago,
                        String distance, HashMap picUrls, Class detailedCrowd) {
        super();
        this.id = id;
        this.title = title;
        this.city = city;
        this.timeago = timeago;
        this.distance = distance;
        this.picUrls = picUrls;
        this.detailedCrowd = detailedCrowd;
    }

    /** Getter functions */
    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCity() {
        return this.city;
    }

    public String getTimeago() {
        return this.timeago;
    }

    public String getDistance() {
        return this.distance;
    }

    public HashMap<Integer, ArrayList<String>> getPicUrls() {
        return this.picUrls;
    }

    public Class<LivePicsGalleryActivity> getDetailedCrowd() {
        return this.detailedCrowd;
    }
}

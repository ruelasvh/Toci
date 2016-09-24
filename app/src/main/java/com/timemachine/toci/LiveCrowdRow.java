package com.timemachine.toci;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor Ruelas on 3/7/16.
 */
public class LiveCrowdRow {

    private String id;
    private String title;
    private String city;
    private String timeago;
    private String distance;
    private HashMap<Integer, ArrayList<String>> picUrls;

    public LiveCrowdRow() {
        // Default constructor
    }

    public LiveCrowdRow(String id, String title, String city, String timeago,
                        String distance, HashMap picUrls) {
        super();
        this.id = id;
        this.title = title;
        this.city = city;
        this.timeago = timeago;
        this.distance = distance;
        this.picUrls = picUrls;
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


    @Override
    public String toString() {
        return "LiveCrowdRow [id=" + id + ", title=" + title + ", " +
                "city=" + city + ", timeago=" + timeago + ", " +
                "distance=" + distance + ", picUrls=" + "]";
    }
}

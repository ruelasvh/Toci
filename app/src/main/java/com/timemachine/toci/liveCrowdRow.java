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
    public HashMap<String, ArrayList<String>> picUrls;
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

}

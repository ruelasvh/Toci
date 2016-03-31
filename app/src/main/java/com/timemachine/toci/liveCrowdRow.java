package com.timemachine.toci;

/**
 * Created by Victor Ruelas on 3/7/16.
 */
public class liveCrowdRow {

    public String id;
    public String title;
    public String city;
    public String subtitle;
    public String distance;
    public String[] picUrls;
    public Class detailedCrowd;

    public liveCrowdRow(String id, String title, String city, String subtitle,
                        String distance, String[] picUrls, Class detailedCrowd) {
        super();
        this.id = id;
        this.title = title;
        this.city = city;
        this.subtitle = subtitle;
        this.distance = distance;
        this.picUrls = picUrls;
        this.detailedCrowd = detailedCrowd;
    }

}

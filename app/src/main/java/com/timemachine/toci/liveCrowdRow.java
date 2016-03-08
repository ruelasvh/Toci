package com.timemachine.toci;

import android.content.Context;
import android.widget.FrameLayout;

/**
 * Created by Victor Ruelas on 3/7/16.
 */
public class liveCrowdRow {

    String imageBaseDirectory = "http://crowdzeeker.com/AppCrowdZeeker/testcrowdpics/";
    String sortScript = "http://crowdzeeker.com/AppCrowdZeeker/fetchlatestcrowd.php";

    public String picUrl;
    public String title;
    public String subtitle;
    public String distance;

    public liveCrowdRow(String picUrl, String title, String subtitle, String distance) {
        super();
        this.picUrl = picUrl;
        this.title = title;
        this.subtitle = subtitle;
        this.distance = distance;
    }

}

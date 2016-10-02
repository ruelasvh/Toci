package com.timemachine.toci;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Victor Ruelas on 9/24/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */

public class SerializeLiveCrowdRow {

    public static String toJson(LiveCrowdRow livecrowdrow) {
        Gson gson = new Gson();
        String json = gson.toJson(livecrowdrow);
        return json;
    }

    public static LiveCrowdRow fromJson(String json) {
        Gson gson = new Gson();
        LiveCrowdRow liveCrowdRow = gson.fromJson(json, LiveCrowdRow.class);
        return liveCrowdRow;
    }

    public static LiveCrowdRow[] fromJson(ArrayList<String> list) {
        if (list != null) {

            ArrayList<LiveCrowdRow> crowds = new ArrayList<>();

            for (String element : list) {
                crowds.add(fromJson(element));
            }

            return (crowds.toArray(new LiveCrowdRow[crowds.size()]));

        } else {

            return null;

        }
    }
}

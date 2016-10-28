package com.timemachine.toci;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Victor Ruelas on 9/24/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */

public class SerializeLiveCrowd {

    public static String toJson(LiveCrowd livecrowd) {
        Gson gson = new Gson();
        String json = gson.toJson(livecrowd);
        return json;
    }

    public static LiveCrowd fromJson(String json) {
        Gson gson = new Gson();
        LiveCrowd liveCrowd = gson.fromJson(json, LiveCrowd.class);
        return liveCrowd;
    }

    public static LiveCrowd[] fromJson(ArrayList<String> list) {
        if (list != null) {

            ArrayList<LiveCrowd> crowds = new ArrayList<>();

            for (String element : list) {
                crowds.add(fromJson(element));
            }

            return (crowds.toArray(new LiveCrowd[crowds.size()]));

        } else {

            return null;

        }
    }
}

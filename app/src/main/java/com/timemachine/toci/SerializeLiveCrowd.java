package com.timemachine.toci;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Victor Ruelas on 9/24/16.
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

    public static ArrayList<String> toJson(LiveCrowd[] crowds){
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < crowds.length; i++) {
            list.add(toJson(crowds[i]));
        }

        return list;
    }

    public static LiveCrowd[] fromJson(ArrayList<String> list) {
        ArrayList<LiveCrowd> crowds = new ArrayList<>();

        for (String element : list) {
            crowds.add(fromJson(element));
        }

        return (crowds.toArray(new LiveCrowd[crowds.size()]));

    }
}

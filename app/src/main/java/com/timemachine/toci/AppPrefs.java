package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Victor Ruelas on 4/5/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AppPrefs {
    private static final String TAG = AppPrefs.class.getSimpleName();
    private static final int MAX_SIZE = 5;
    private static final String USER_PREFS = "USER_PREFS";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String fav_cities = "fav_cities_prefs";
    private String fav_crowds = "fav_crowds_prefs";
    private String fav_crowdsv2 = "fav_crowds_prefs";
    private String fav_crowdsv3 = "fav_crowds_prefs";
    private String user_name = "user_name_prefs";
    private String user_id = "user_id_prefs";
    private Set<String> in;
    private Set<String> out;
    private Set<LiveCrowdRow> crowd_in;
    private Set<LiveCrowdRow> crowd_out;
    private JSONArray jsonArrayIn;
    private JSONArray jsonArrayOut;
    private ArrayList<LiveCrowdRow> crowds_in;
    private LiveCrowdRow[] crowds_out;

    public AppPrefs(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Getter methods
    public Set<String> getFavorite_cities() {
        return appSharedPrefs.getStringSet(fav_cities, null);
    }


    public String getFav_crowdsv4() {
        return appSharedPrefs.getString(fav_crowds, null);
    }

    // Setter methods
    public void setFavorite_city(String _favorite_city) {
        out = appSharedPrefs.getStringSet(fav_cities, new HashSet<String>());
        if (out.size() == MAX_SIZE) {
            // Reached max size of city favorites/override
            in = new HashSet<>(out);
            in.remove(in.toArray()[4]);
            in.add(_favorite_city);
            prefsEditor.putStringSet(fav_cities, in).commit();
        } else {
            in = new HashSet<>(out);
            in.add(_favorite_city);
            prefsEditor.putStringSet(fav_cities, in);
            prefsEditor.commit();
        }
    }


    public void setFav_crowdsv4(LiveCrowdRow liveCrowdRow) {
        Gson gson = new Gson();
        String json = gson.toJson(liveCrowdRow);
        prefsEditor.putString(fav_crowds, json);
        prefsEditor.commit();
    }


    // Methods to remove favorites
    public void removeFavorite_city(String _favorite_city) {
        out = appSharedPrefs.getStringSet(fav_cities, new HashSet<String>());
        in = new HashSet<>(out);
        in.remove(_favorite_city);
        prefsEditor.putStringSet(fav_cities, in);
        prefsEditor.commit();
    }


    public void removeFavorite_crowd(String _favorite_crowd) {
        out = appSharedPrefs.getStringSet(fav_crowds, new HashSet<String>());
        in = new HashSet<>(out);
        in.remove(_favorite_crowd);
        prefsEditor.putStringSet(fav_crowds, in);
        prefsEditor.commit();
    }

    /** Methods for dealing with user accounts **/
    public int getUser_id() {
        return appSharedPrefs.getInt(user_id, 0);
    }

    public void setUser_id(int _user_id) {
        prefsEditor.putInt(user_id, _user_id).commit();
    }

    public String getUser_name() {
        return appSharedPrefs.getString(user_name, "unknown");
    }

    public void setUser_name(String _user_name) {
        prefsEditor.putString(user_name, _user_name).commit();
    }

}

package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Victor Ruelas on 4/5/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AppPrefs {
    private static final String USER_PREFS = "USER_PREFS";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String user_name = "user_name_prefs";
    private String user_id = "user_id_prefs";
    private Set<String> favorite_cities;

    public AppPrefs(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        this.favorite_cities = new HashSet<>();
    }

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

    public Set<String> getFavorite_cities() {
        return appSharedPrefs.getStringSet("favorite_cities", null);
    }

    public void setFavorite_city(String _favorite_city) {
        favorite_cities.add(_favorite_city);
        prefsEditor.putStringSet("favorite_cities", favorite_cities).commit();
    }

    public void removeFavorite_city(String _favorite_city) {
        favorite_cities.remove(_favorite_city);
        prefsEditor.putStringSet("favorite_cities", favorite_cities).commit();
    }

}

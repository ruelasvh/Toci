package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
    private String user_name = "user_name_prefs";
    private String user_id = "user_id_prefs";
    private Set<String> in;
    private Set<String> out;
    private Set<String> crowds_in;
    private Set<String> crowds_out;


    public AppPrefs(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Getter methods
    public Set<String> getFavorite_cities() {
        return appSharedPrefs.getStringSet(fav_cities, null);
    }

    public Set<String> getFav_crowds() {
        return appSharedPrefs.getStringSet(fav_crowds, (new Set<String>() {
            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }
        }));
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

    public void setFav_crowds(LiveCrowdRow liveCrowdRow) {
        crowds_out = appSharedPrefs.getStringSet(fav_crowds, new HashSet<String>());
        crowds_in = new HashSet<>(crowds_out);
        crowds_in.add(SerializeLiveCrowdRow.toJson(liveCrowdRow));
        prefsEditor.putStringSet(fav_crowds, crowds_in);
        prefsEditor.commit();
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

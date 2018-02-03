package com.timemachine.toci;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
public class AppPrefs {
    private static final String USER_PREFS = "USER_PREFS";
    private static final String FAV_CROWDS = "FAV_CROWDS";
    private static final String SESSION_STATUS = "SESSION_STATUS";
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int REQUEST_ACCESS_CAMERA = 2;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Set<String> crowds_in;
    private Set<String> crowds_out;
    private Context mContext;


    public AppPrefs(Context context) {
        this.mContext = context;
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public Set<String> getFavorite_crowds() {
        return appSharedPrefs.getStringSet(FAV_CROWDS, new HashSet<String>());
    }

    public void setFavorite_crowd(String crowd) {
        crowds_out = appSharedPrefs.getStringSet(FAV_CROWDS, new HashSet<String>());
        crowds_in = new HashSet<>(crowds_out);
        crowds_in.add(crowd);
        prefsEditor.putStringSet(FAV_CROWDS, crowds_in).commit();
    }


    /**
     * Methods to remove favorites
     * @param crowd
     */
    public void removeFavorite_crowd(String crowd) {
        crowds_out = appSharedPrefs.getStringSet(FAV_CROWDS, new HashSet<String>());
        crowds_in = new HashSet<>(crowds_out);
        crowds_in.remove(crowd);
        prefsEditor.putStringSet(FAV_CROWDS, crowds_in).commit();
    }

    /** Methods for dealing with user accounts **/
    public boolean getSessionStatus() {
        return  appSharedPrefs.getBoolean(SESSION_STATUS, false);
    }

    public void setSessionStatus(boolean _is_loggedin) {
        prefsEditor.putBoolean(SESSION_STATUS, _is_loggedin).commit();
    }

    public boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return this.mContext.checkSelfPermission(ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setLocationPermissions() {
        ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
    }

    public boolean hasCameraAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return this.mContext.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setCameraPermissions() {
        ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{CAMERA}, REQUEST_ACCESS_CAMERA);
    }
}

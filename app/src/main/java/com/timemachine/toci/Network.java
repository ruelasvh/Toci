package com.timemachine.toci;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Victor Ruelas on 11/17/16.
 */

public class Network {

    Context context;

    public Network(Context context) {
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}

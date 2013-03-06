package de.softgames.sdk.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * The Class CheckNetwork manages the network's status related tasks
 */
public class NetworkUtilities {

    private static final String TAG = "CheckNetwork";

    private static ConnectivityManager cm;

    private static NetworkInfo netInfo;

    /**
     * Checks if is online.
     * 
     * @param ctx
     *            the app context
     * @return true, if is online or connecting
     */
    public static boolean isOnline(Context ctx) {
        Log.d(TAG, "isOnline()?...");
        initConnManager(ctx);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(TAG, "Device with online status!");
            return true;
        }
        Log.d(TAG, "The device is offline");
        return false;
    }

    public static boolean isConnectedViaWifi(Context ctx) {
        initConnManager(ctx);
        netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private static void initConnManager(Context ctx) {
        cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

    }

}

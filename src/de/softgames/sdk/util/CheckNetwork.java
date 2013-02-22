package de.softgames.sdk.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import de.softgames.sdk.ui.SoftgamesUI;


/**
 * The Class CheckNetwork manages the network's status related tasks
 */
public class CheckNetwork {

    private static final String TAG = "CheckNetwork";

    /**
     * Checks if is online.
     * 
     * @param ctx
     *            the app context
     * @return true, if is online or connecting
     */
    public static boolean isOnline(Context ctx) {
        Log.d(TAG, "isOnline()?...");
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(TAG, "Device with online status!");
            return true;
        }
        Log.d(TAG, "The device is offline");
        return false;
    }

    /**
     * Force internet connection.
     * 
     * @param ctx
     *            the context, this must be an Activity context
     */
    public static void forceInternetConnection(Context ctx) {
        Log.d(TAG, "forceInternetConnection()");
        if (!isOnline(ctx)) {
            SoftgamesUI.buildRetryConnectionDialog(ctx);
        }

    }

}

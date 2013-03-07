package de.softgames.sdk.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * The Class CheckNetwork manages the network's status related tasks
 */
public class NetworkUtilities {

    private static final String TAG = "CheckNetwork";

    private static ConnectivityManager connManager;

    private static TelephonyManager telephonyManager;

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
        netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(TAG, "Device with online status!");
            return true;
        }
        Log.d(TAG, "The device is offline");
        return false;
    }

    public static boolean isConnectedViaWifi(Context ctx) {
        initConnManager(ctx);
        netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Gets the connection type.
     * 
     * @param ctx
     *            the ctx
     * @return the connection type
     */
    public static int getConnectionType(Context ctx) {
        initTelManager(ctx);
        
        if (isConnectedViaWifi(ctx)) {
            return NetworkType.WIFI.getValue();
        }

        int cType = telephonyManager.getNetworkType();
        switch (cType) {
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return NetworkType.EDGE.getValue();
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return NetworkType.GPRS.getValue();

        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return NetworkType.HSDPA.getValue();
        default:
            return NetworkType.UNKNOWN.getValue();
        }
        
    }

    /**
     * Inits the android connection manager.
     * 
     * @param ctx
     *            the ctx
     */
    private static void initConnManager(Context ctx) {
        connManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    /**
     * Inits the tel manager.
     * 
     * @param ctx
     *            the ctx
     */
    private static void initTelManager(Context ctx) {
        telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

}

package de.softgames.sdk.util;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;


/**
 * The Class CheckNetwork manages the network's status related tasks
 */
public class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getSimpleName();

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
     * Gets the local IP address.
     *
     * @return the IP address
     */
    public static String getLocalIpAddress(Context ctx) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr
                            .nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
        
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

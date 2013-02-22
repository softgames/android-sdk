package de.softgames.sdk;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import de.softgames.sdk.ui.SoftgamesUI;
import de.softgames.sdk.util.SGSettings;
import de.softgames.sdk.util.ServerUtilities;


/**
 * The Class GCMIntentService.
 */
public class GCMIntentService extends GCMBaseIntentService {

    /** The Constant TAG. */
    private static final String TAG = "GCMIntentService";

    /**
     * Instantiates a new GCM intent service.
     */
    public GCMIntentService() {
        super(SGSettings.SENDER_ID);
        Log.e(TAG, "Instantiating service");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content
     * .Context, java.lang.String)
     */
    public void onRegistered(Context context, String regId) {
        Log.d(TAG, "onRegistered: " + regId);
        ServerUtilities.register(context, regId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content
     * .Context, java.lang.String)
     */
    public void onUnregistered(Context context, String regId) {
        Log.d(TAG, "onUnregistered: " + regId);
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, regId);
        } else {
            /**
             * This callback results from the call to unregister made on
             * ServerUtilities when the registration to the server failed.
             */
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBaseIntentService#onMessage(android.content
     * .Context, android.content.Intent)
     */
    public void onMessage(Context context, Intent intent) {
        Log.d(TAG, "onMessage: New message");
        SoftgamesUI.generateSGNotification(context, intent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context
     * , java.lang.String)
     */
    public void onError(Context context, String errorId) {
        Log.d(TAG, "onError: " + errorId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBaseIntentService#onRecoverableError(android
     * .content.Context, java.lang.String)
     */
    public boolean onRecoverableError(Context context, String errorId) {
        Log.d(TAG, "onRecoverableError: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

}

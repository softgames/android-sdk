package de.softgames.sdk;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import de.softgames.sdk.ui.SoftgamesUI;
import de.softgames.sdk.util.SGSettings;
import de.softgames.sdk.util.ServerUtilities;


public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SGSettings.SENDER_ID);
        Log.e(TAG, "Instantiating service");
    }

    public void onRegistered(Context context, String regId) {
        Log.d(TAG, "onRegistered: " + regId);
        ServerUtilities.register(context, regId);
    }

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

    public void onMessage(Context context, Intent intent) {
        Log.d(TAG, "onMessage: New message");
        SoftgamesUI.generateSGNotification(context, intent);
    }

    public void onError(Context context, String errorId) {
        Log.d(TAG, "onError: " + errorId);
    }

    public boolean onRecoverableError(Context context, String errorId) {
        Log.d(TAG, "onRecoverableError: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

}

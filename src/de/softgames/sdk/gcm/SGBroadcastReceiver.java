package de.softgames.sdk.gcm;


import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

public class SGBroadcastReceiver extends GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        Log.e("SGBroadcastReceiver", "getting service class name...");
        return "de.softgames.sdk.GCMIntentService";
    }

}

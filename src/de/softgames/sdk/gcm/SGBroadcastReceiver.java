package de.softgames.sdk.gcm;


import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;


/**
 * The Class SGBroadcastReceiver is necessary to get the
 * {@link de.softgames.sdk.GCMIntentService Intent service} from a custom
 * location
 */
public class SGBroadcastReceiver extends GCMBroadcastReceiver {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gcm.GCMBroadcastReceiver#getGCMIntentServiceClassName
     * (android.content.Context)
     */
    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        Log.d("SGBroadcastReceiver", "getting service class name...");
        return "de.softgames.sdk.GCMIntentService";
    }

}

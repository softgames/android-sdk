package de.softgames.sdk;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import de.softgames.sdk.util.SGSettings;
import de.softgames.sdk.util.ServerUtilities;


public class SGRegistrator {

    private static final String TAG = "SGRegistrator";
    private Context ctx;
    private AsyncTask<Void, Void, Void> _registerTask;

    public SGRegistrator(Context ctx) {
        this.ctx = ctx;
    }

    public void registerMe() {
        Log.d(TAG, "registerMe() method invoked...");
        GCMRegistrar.checkDevice(ctx);
        GCMRegistrar.checkManifest(ctx);
        final String regId = GCMRegistrar.getRegistrationId(ctx);

        if (regId == null || regId.equals("")) {
            Log.d(TAG, "regId is empty");
            GCMRegistrar.register(ctx, SGSettings.SENDER_ID);
        } else {
            Log.d(TAG, "registered with regId: " + regId);
            GCMRegistrar.setRegisteredOnServer(ctx, false);
            if (!GCMRegistrar.isRegisteredOnServer(ctx)) {

                _registerTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(ctx,
                                regId);
                        /**
                         * At this point all attempts to register with the app
                         * server failed, so we need to unregister the device
                         * from GCM - the app will try to register again when it
                         * is restarted. Note that GCM will send an unregistered
                         * callback upon completion, but
                         * GCMIntentService.onUnregistered() will ignore it.
                         */
                        if (!registered) {
                            GCMRegistrar.unregister(ctx);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        _registerTask = null;
                    }

                };
                _registerTask.execute(null, null, null);
            }
        }
    }

    public void killTask() {
        if (_registerTask != null) {
            _registerTask.cancel(true);
        }
        try {
            GCMRegistrar.onDestroy(ctx);
        } catch (Exception e) {
            Log.e(TAG, "an error occurred destroying the Activity", e);
        }
    }

}

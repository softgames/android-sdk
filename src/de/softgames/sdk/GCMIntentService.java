package de.softgames.sdk;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import de.softgames.sdk.R;
import de.softgames.sdk.util.SGSettings;
import de.softgames.sdk.util.ServerUtilities;


public class GCMIntentService extends GCMBaseIntentService {

    private static int messageId = 0;
    private static final String STAG = "GCMIntentService";
    private Class<?> launcherActivity;

    public GCMIntentService() {
        super(SGSettings.SENDER_ID);
        Log.e(STAG, "Instantiating service");
    }

    public void onRegistered(Context context, String regId) {
        Log.d(STAG, "onRegistered: " + regId);
        ServerUtilities.register(context, regId);
    }

    public void onUnregistered(Context context, String regId) {
        Log.d(STAG, "onUnregistered: " + regId);
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, regId);
        } else {
            /**
             * This callback results from the call to unregister made on
             * ServerUtilities when the registration to the server failed.
             */
            Log.i(STAG, "Ignoring unregister callback");
        }
    }

    public void onMessage(Context context, Intent intent) {
        Log.d(STAG, "onMessage: New message");
        generateNotification(context, intent);
    }

    public void onError(Context context, String errorId) {
        Log.d(STAG, "onError: " + errorId);
    }

    public boolean onRecoverableError(Context context, String errorId) {
        Log.d(STAG, "onRecoverableError: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    // FIXME encapsulate in other class
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, Intent intent) {

        // The launcher activity set by the user as entry point is instantiated
        launcherActivity = SGSettings.getLauncherActivity();

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        Log.v(STAG, "Received message: " + title);
        //TODO add time and large icon, icons provided by us
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentText(message).setContentTitle(title);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, launcherActivity);

        /**
         * The stack builder object will contain an artificial back stack for
         * the started Activity. This ensures that navigating backward from the
         * Activity leads out of your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(launcherActivity);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // _message_id allows you to update the notification later on.
        notificationManager.notify(messageId++, mBuilder.build());
    }

}

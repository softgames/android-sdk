/**
 * 
 */
package de.softgames.sdk.ui;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import de.softgames.sdk.R;
import de.softgames.sdk.model.SoftgamesNotification;
import de.softgames.sdk.util.SGSettings;


/**
 * @author rolandcastillo
 *
 */
public final class SoftgamesUI {

    private static final String TAG = "SoftgamesUI";
    private static Class<?> launcherActivity;
    private static int messageId = 0;
    private static Resources res;

    public static void generateSGNotification(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        SoftgamesNotification softgamesNotification = new SoftgamesNotification(
                title, message);

        generateNotification(context, softgamesNotification);
    }

    public static void generateTestNotification(Context context) {
        String message = "Test title";
        String title = "test";

        SoftgamesNotification softgamesNotification = new SoftgamesNotification(
                title, message);

        generateNotification(context, softgamesNotification);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     * 
     * @param context
     *            the context
     * @param sgNotification
     *            the object SoftgamesNotification
     *            {@link de.softgames.sdk.model.SoftgamesNotification}
     */
    public static void generateNotification(Context context,
            SoftgamesNotification sgNotification) {

        // The launcher activity set by the user as entry point is instantiated
        launcherActivity = SGSettings.getLauncherActivity();
        // Initialize the resources
        res = context.getResources();

        String title = sgNotification.getTitle();
        String message = sgNotification.getMessage();

        Log.v(TAG, "Received message: " + title);

        Bitmap largeNotificationIcon = createLargeIconBitmap(R.drawable.sg_ic_notify_msg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.sg_ic_notify_msg)
                .setLargeIcon(largeNotificationIcon).setContentText(message)
                .setContentTitle(title);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, launcherActivity);

        /**
         * The stack builder object will contain an artificial back stack for
         * the started Activity. This ensures that navigating backward from the
         * Activity leads out of your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(launcherActivity);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // _message_id allows you to update the notification later on.
        notificationManager.notify(messageId++, mBuilder.build());
    }

    /**
     * Creates the large icon bitmap.
     * 
     * @param sg_ic_notify_msg
     *            the sg_ic_notify_msg
     * @return the bitmap
     */
    private static Bitmap createLargeIconBitmap(int sg_ic_notify_msg) {

        if (res != null) {

            BitmapDrawable largeNotificationIconDrawable = (BitmapDrawable) res
                    .getDrawable(sg_ic_notify_msg);
            Bitmap largeNotificationIcon = largeNotificationIconDrawable
                    .getBitmap();
            int height = (int) res
                    .getDimension(R.dimen.notification_large_icon_height);
            int width = (int) res
                    .getDimension(R.dimen.notification_large_icon_width);
            largeNotificationIcon = Bitmap.createScaledBitmap(
                    largeNotificationIcon, width, height, false);

            return largeNotificationIcon;

        } else {
            Log.e(TAG, "The res variable is not initialized");
            return null;
        }

    }

}

/**
 * 
 */
package de.softgames.sdk.ui;


import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import de.softgames.sdk.OpenxAdView;
import de.softgames.sdk.R;
import de.softgames.sdk.model.SoftgamesNotification;
import de.softgames.sdk.util.SGSettings;


/**
 * @author rolandcastillo
 * 
 */
public final class SoftgamesUI {

    /** The Constant TAG. */
    private static final String TAG = SoftgamesUI.class.getSimpleName();

    /** The launcher activity. */
    private static Class<?> launcherActivity;

    /** The message id. */
    private static int messageId = 0;

    /** The res. */
    private static Resources res;

    /**
     * Generate a custom notification with the info received from our
     * notifications web tool(AKA uhura).
     * 
     * @param context
     *            the context
     * @param intent
     *            the intent
     */
    public static void generateSGNotification(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        SoftgamesNotification softgamesNotification = new SoftgamesNotification(
                title, message);

        generateNotification(context, softgamesNotification);
    }

    /**
     * Generate test notification.
     * 
     * @param context
     *            the context
     */
    public static void generateTestNotification(Context context) {
        String message = "Test Message";
        String title = "Foo Bar";

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

        Log.d(TAG, "Received message: " + title);

        // Bitmap largeNotificationIcon =
        // createLargeIconBitmap(R.drawable.sg_ic_notify_msg);
        // TODO Determine how is going to be set the large icon

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.sg_ic_notify_msg)
                .setContentText(message).setContentTitle(title);

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
    @SuppressWarnings("unused")
    private static Bitmap createLargeIconBitmap(int sg_ic_notify_msg) {

        if (res != null) {

            BitmapDrawable largeNotificationIconDrawable = (BitmapDrawable) res
                    .getDrawable(sg_ic_notify_msg);
            Bitmap largeNotificationIcon = largeNotificationIconDrawable
                    .getBitmap();
            int height = (int) res
                    .getDimension(R.dimen.sg_notification_large_icon_height);
            int width = (int) res
                    .getDimension(R.dimen.sg_notification_large_icon_width);
            largeNotificationIcon = Bitmap.createScaledBitmap(
                    largeNotificationIcon, width, height, false);

            return largeNotificationIcon;

        } else {
            Log.e(TAG, "The res variable is not initialized");
            return null;
        }
    }

    public static Dialog buildMoreGamesDialog(final Context mCtx) {

        final Dialog dialog = new Dialog(mCtx, R.style.SGMoreGamesDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sg_dialog_more_games);
        dialog.setCancelable(true);

        OpenxAdView crossPromoAdView = (OpenxAdView) dialog
                .findViewById(R.id.adview_xpromo);
        crossPromoAdView.load();

        ImageButton buttonClose = (ImageButton) dialog
                .findViewById(R.id.sg_dialog_more_games_btn_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramView) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * Gets the current device screen density
     * 
     * @param an
     *            instance of WindowManager
     * @return a float with the value of the screen's density
     */
    public static float getScreenDensity(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        float screenDensity = metrics.density;
        return screenDensity;
    }

    /**
     * Animation object coming from the right side
     * 
     * @return Animation object
     */
    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * out Animation to the left
     * 
     * @return
     */
    public static Animation outToLeftAnimation() {
        Animation outToLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -2.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToLeft.setDuration(500);
        outToLeft.setInterpolator(new AccelerateInterpolator());
        return outToLeft;
    }

}

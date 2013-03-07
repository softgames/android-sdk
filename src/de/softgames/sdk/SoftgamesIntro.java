/**
 * Activity class that shows a splash screen and some ads when a Game uses the SDK
 *  
 */
package de.softgames.sdk;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ViewFlipper;
import de.softgames.sdk.exceptions.IllegalLauncherActivityException;
import de.softgames.sdk.model.SoftgamesAd;
import de.softgames.sdk.ui.SoftgamesUI;
import de.softgames.sdk.util.NetworkUtilities;
import de.softgames.sdk.util.SGSettings;


/**
 * The Class SoftgamesIntro.
 * 
 * @author rolandcastillo
 */
public class SoftgamesIntro extends Activity {

    /** The Constant TAG. */
    private static final String TAG = "SoftgamesIntro";

    /** The number of threads to keep in the pool. */
    private static final int POOL_SIZE = 3;

    /** The schedule task executor. */
    private ScheduledExecutorService scheduleTaskExecutor;

    /** The launcher activity. */
    private Class<?> launcherActivity = null;

    /** The resources */
    protected Resources res;

    /** The Flipper to flip between the splash screen and the ads */
    private ViewFlipper flipper;

    private OpenxAdView adView;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We want to show the splash screen and the ads in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Let's initialize the ad related objects
        initOpenxAds();

        setContentView(R.layout.sg_flipper);
        res = getResources();
        flipper = (ViewFlipper) findViewById(R.id.softgames_master);


        // Log info for debug purposes
        adView = (OpenxAdView) findViewById(R.id.adview);


        scheduleTaskExecutor = Executors.newScheduledThreadPool(POOL_SIZE);
        // Thread to display a splash screen during the given seconds
        scheduleTaskExecutor.schedule(new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAd();
                    }
                });
            }

        }, SGSettings.SPLASH_DELAY, TimeUnit.SECONDS);

    }

    /**
     * initializes the necessary objects to display ads.
     */
    private void initOpenxAds() {
        // Gets an instance of window manager for display related tasks
        WindowManager windowManager = getWindowManager();
        // The density is gather in order to determine the pixel ratio
        Float density = SoftgamesUI.getScreenDensity(windowManager);

        Display display = windowManager.getDefaultDisplay();
        SoftgamesAd softgamesAd = new SoftgamesAd(SGSettings.getGameName(),
                display.getWidth(), display.getHeight(), density,
                NetworkUtilities.getConnectionType(getApplicationContext()));
        Log.d(TAG, softgamesAd.toString());
        OpenxAdView.setSoftgamesAd(softgamesAd);
    }

    /**
     * The ad's layout is requested with its respective banner
     * 
     */
    private void showAd() {
        if (SGSettings.isInternetRequired()) {
            requestAd();
        } else {
            startApp();
        }

    }

    /**
     * The activity set as
     * {@link de.softgames.sdk.util.SGSettings#launcherActivity
     * launcherActivity} is started
     */
    private void startApp() {
        Log.d(TAG, "startApp()");
        try {
            // The launcher activity set by the user as entry point is
            // instantiated
            launcherActivity = SGSettings.getLauncherActivity();
            Log.d(TAG, "Starting the Activty indicated as entry point");
            Intent intent = new Intent(this, launcherActivity);
            startActivity(intent);
        } catch (IllegalLauncherActivityException e) {
            Log.e(TAG, "The entry point activity is NULL");
        } catch (Exception e) {
            Log.e(TAG, "An error ocurred while starting the given activity.");
        }

        finish();
    }

    /**
     * Requests an ad and displays it during the given seconds
     */
    private void requestAd() {
        Log.d(TAG, "requestAd()");
        long adDelay = SGSettings.AD_DELAY;
        if (!NetworkUtilities.isOnline(this)) {
            buildRetryConnectionDialog();
        } else {
            try {
                adView.load();
                Log.e(TAG, adView.getZoneTemplate(adView.getZoneID()));
                // flipper.setInAnimation(SoftgamesUI.inFromRightAnimation());
                flipper.showNext();

                // Thread to show the ads during the given seconds
                scheduleTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        startApp();
                    }
                }, adDelay, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        scheduleTaskExecutor.shutdown();
        super.onDestroy();
    }

    /**
     * Builds the retry connection dialog.
     * 
     * @param ctx
     *            the context
     */
    public void buildRetryConnectionDialog() {
        Log.d(TAG, "buildRetryConnectionDialog()");
        res = this.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(res.getString(R.string.offline_retry_msg));
        builder.setCancelable(true);

        builder.setPositiveButton(res.getString(R.string.button_retry),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        requestAd();
                    }
                });

        builder.setNegativeButton(res.getString(R.string.button_exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dlg = builder.create();
        dlg.show();
    }

}

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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ViewFlipper;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

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

    /** The resources. */
    protected Resources res;

    /** The Flipper to flip between the splash screen and the ads. */
    private ViewFlipper flipper;

    /** The openx custom view. */
    private OpenxAdView adView;

    private GoogleAnalytics mGaInstance;

    private Tracker mTracker;
    
    private static final int XPROMO_ID = 1;
    
    private static final int LOADING_ID = 2;


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

        // Initializes the GA tracker object
        initGoogleAnalytics();

        // Let's initialize the ad related objects
        initOpenxAds();

        setContentView(R.layout.sg_flipper);
        res = getResources();
        flipper = (ViewFlipper) findViewById(R.id.softgames_master);

        // Log info for debug purposes
        adView = (OpenxAdView) findViewById(R.id.adview);
        // TODO Instantiate a new openx object

        scheduleTaskExecutor = Executors.newScheduledThreadPool(POOL_SIZE);
        // Thread to display a splash screen during the given seconds
        scheduleTaskExecutor.schedule(new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFirstSession()) {
                            showLoadingScreen();
                        } else {
                            showCrosspromotion();
                        }

                    }
                });
            }
        }, SGSettings.SPLASH_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Inits the google analytics.
     */
    private void initGoogleAnalytics() {
        // Get the GoogleAnalytics singleton.
        mGaInstance = GoogleAnalytics.getInstance(this);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mTracker = mGaInstance.getTracker("UA-39037923-1");
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
     * The ad's layout is requested with its respective banner.
     */
    private void showLoadingScreen() {
        Log.d(TAG, "showLoadingScreen");
        if (SGSettings.isInternetRequired()) {
            requestAd();
        } else {
            startApp();
        }
    }

    private void showCrosspromotion() {
        Log.d(TAG, "showCrosspromotion");
        if (SGSettings.isInternetRequired()) {
            if (!NetworkUtilities.isOnline(this)) {
                buildRetryConnectionDialog();
            } else {
                try {
                    // adView.load();
                    // flipper.setInAnimation(SoftgamesUI.inFromRightAnimation());
                    flipper.setDisplayedChild(XPROMO_ID);

                    // Thread to show the ads during the given seconds
                    scheduleTaskExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    showLoadingScreen();
                                }
                            });
                        }
                    }, SGSettings.X_PROMO_DELAY, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "error", e);
                }
            }
        } else {
            startApp();
        }
    }

    /**
     * The activity set as.
     * 
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
     * Requests an ad and displays it during the given seconds.
     */
    private void requestAd() {
        Log.d(TAG, "requestAd()");
        long adDelay = SGSettings.AD_DELAY;
        if (!NetworkUtilities.isOnline(this)) {
            buildRetryConnectionDialog();
        } else {
            try {
                adView.load();
                // Log.e(TAG, adView.getZoneTemplate(adView.getZoneID()));
                // flipper.setInAnimation(SoftgamesUI.inFromRightAnimation());
                flipper.setDisplayedChild(LOADING_ID);

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

    /**
     * Checks if is first session.
     * 
     * @return true, if is first session
     */
    protected boolean isFirstSession() {
        try {
            // Restore preferences
            SharedPreferences sgSettings = getSharedPreferences(
                    SGSettings.PREFS_NAME, 0);

            boolean firstSession = sgSettings.getBoolean(
                    SGSettings.FIRST_SESSION, true);

            if (firstSession) {
                Log.d(TAG, "This is the first session");
                SharedPreferences.Editor editor = sgSettings.edit();
                editor.putBoolean(SGSettings.FIRST_SESSION, false);
                editor.commit();
                return true;
            } else {
                Log.d(TAG, "This is an old user");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTracker.sendView("/SoftgamesIntro");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // private View getNext(int next) {
    // return null;
    // }

}

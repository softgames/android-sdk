/**
 * Activity class that shows a splash screen and some ads when a Game uses the SDK
 *  
 */
package de.softgames.sdk;


import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import de.softgames.sdk.exceptions.IllegalLauncherActivityException;
import de.softgames.sdk.model.SoftgamesAd;
import de.softgames.sdk.ui.SoftgamesUI;
import de.softgames.sdk.util.NetworkType;
import de.softgames.sdk.util.NetworkUtilities;
import de.softgames.sdk.util.SGSettings;


/**
 * The Softgames Activity
 * 
 * @author roland.castillo@softgames.de
 */
public class SoftgamesActivity extends Activity implements OnClickListener {

    /** The Constant TAG. */
    private static final String TAG = SoftgamesActivity.class.getSimpleName();

    /** The number of threads to keep in the pool. */
    private static final int POOL_SIZE = 3;

    protected static final int ACTIVITY_RESULT_SETTINGS = 10;

    /** The schedule task executor. */
    private ScheduledExecutorService scheduleTaskExecutor;

    /** The launcher activity. */
    private Class<?> launcherActivity = null;

    /** The resources. */
    protected Resources res;

    private RelativeLayout layoutContainer;

    private LinearLayout crossPromotionLayout;

    private LinearLayout loadingScreenLayout;

    /** The openx custom view. */
    private OpenxAdView loadingScreenAdView;

    private OpenxAdView crossPromoAdView;

    private ImageView teaserImage;

    private Button buttonPlay;

    private Tracker mTracker;

    // Registrator object used to establish a communication with Google Cloud
    // messaging
    public SGRegistrator registrator;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sg_master_layout);

        res = getResources();
        layoutContainer = (RelativeLayout) findViewById(R.id.softgames_master);
        crossPromotionLayout = (LinearLayout) findViewById(R.id.xpromo);
        loadingScreenLayout = (LinearLayout) findViewById(R.id.adsLayout);

        /*
         * This object is needed to get working the push notifications.
         */
        registrator = new SGRegistrator(this);
        /*
         * this method must be invoked in order to register the device on the
         * softgames server
         */
        registrator.registerMe();

        // The portrait orientation is default
        int orientationId = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        if (SGSettings.isOrientationLandscape()) {
            orientationId = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }

        // set the orientation for the ads once the game orientation is
        // determined
        // TODO enable for production
        // setRequestedOrientation(orientationId);

        layoutContainer.startAnimation(SoftgamesUI.inFromRightAnimation());

        // We want to show the splash screen and the ads in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Keep screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // The google analytics object instance
        GoogleAnalytics mInstance = GoogleAnalytics.getInstance(this);
        // Get the existing tracker
        mTracker = mInstance.getDefaultTracker();

        // Let's initialize the ad related objects
        initOpenxAds();

        // The Openx ads are instantiated
        loadingScreenAdView = (OpenxAdView) findViewById(R.id.adview);
        crossPromoAdView = (OpenxAdView) findViewById(R.id.adview_xpromo);
        // The green button shown in the xpromo screen
        buttonPlay = (Button) findViewById(R.id.button_play);

        // Custom type face
        TextView xpromoDividerText = (TextView) findViewById(R.id.divider_text);
        TextView teaserGameName = (TextView) findViewById(R.id.teaser_text);

        // First let's get the app name from the strings file
        String sGameName = res.getString(R.string.app_name);

        // If the name was set programatically
        if (SGSettings.getGameName() != null && SGSettings.getGameName() != "") {
            sGameName = SGSettings.getGameName();
        }

        teaserGameName.setText(sGameName);

        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(),
                    "oswald.ttf");
            xpromoDividerText.setTypeface(typeface);
            teaserGameName.setTypeface(typeface, 1);
        } catch (Exception e) {
            Log.e(TAG, "The font oswald_bold is missing from the assets folder");
        }

        teaserImage = (ImageView) findViewById(R.id.teaserImage);
        if (SGSettings.getTeaserImage() != null) {
            teaserImage.setImageDrawable(SGSettings.getTeaserImage());
        }

        scheduleTaskExecutor = Executors.newScheduledThreadPool(POOL_SIZE);

        if (isFirstSession()) {
            // showLoadingScreen();
            startApp();
        } else {
            showCrosspromotion();
        }

        buttonPlay.setOnClickListener(this);

    }

    /**
     * initializes the necessary objects to display ads.
     */
    private void initOpenxAds() {
        String language = "", countryCode = "";
        // Gets an instance of window manager for display related tasks
        WindowManager windowManager = getWindowManager();
        // The density is gather in order to determine the pixel ratio
        Float density = SoftgamesUI.getScreenDensity(windowManager);

        String packageName = getApplicationContext().getPackageName();
        Display display = windowManager.getDefaultDisplay();
        try {
            Locale locale = getResources().getConfiguration().locale;
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getSimCountryIso();
            if (countryCode == null || countryCode.equals("")) {
                countryCode = locale.getCountry();
            }
            language = locale.getDisplayLanguage();
        } catch (Exception e) {
            Log.e(TAG,
                    "There was an error getting the language and country code");
        }

        int connectionType = NetworkUtilities
                .getConnectionType(getApplicationContext());
        SoftgamesAd softgamesAd = new SoftgamesAd(packageName,
                display.getWidth(), display.getHeight(), density,
                connectionType, Build.MANUFACTURER, language, countryCode);
        Log.d(TAG, softgamesAd.toString());
        OpenxAdView.setSoftgamesAd(softgamesAd);

        String sInternetStatus = "no";
        if (connectionType != NetworkType.UNKNOWN.getValue()) {
            sInternetStatus = "yes";
        }
        mTracker.sendEvent("internet_connection", sInternetStatus,
                Long.valueOf(connectionType) + "", Long.valueOf(connectionType));
    }

    /**
     * Shows the screen with the cross promotion from openx.
     */
    private void showCrosspromotion() {
        Log.d(TAG, "showCrosspromotion()");
        if (!NetworkUtilities.isOnline(this)) {
            if (SGSettings.isInternetRequired()) {
                buildRetryConnectionDialog();
            } else {
                startApp();
            }
        } else {
            try {
                crossPromoAdView.load();
                mTracker.sendView("/CrossPromotionPage");

            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
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
        registrator.killTask();
        try {
            // The launcher activity set by the user as entry point is
            // instantiated
            launcherActivity = SGSettings.getLauncherActivity();
            Log.d(TAG, "Starting the Activty indicated as entry point");
            mTracker.sendView("/GameStarted");
            Intent intent = new Intent(this, launcherActivity);
            startActivity(intent);
        } catch (IllegalLauncherActivityException e) {
            Log.e(TAG, "The entry point activity is NULL");
        } catch (Exception e) {
            Log.e(TAG, "An error ocurred while starting the given activity.");
        }

    }

    /**
     * Requests an ad and displays it during the given seconds.
     */
    private void showLoadingScreen() {
        long adDelay = SGSettings.AD_DELAY;
        if (!NetworkUtilities.isOnline(this)) {
            if (SGSettings.isInternetRequired()) {
                buildRetryConnectionDialog();
            } else {
                startApp();
            }

        } else {
            try {
                loadingScreenAdView.loadInIframe();
                crossPromotionLayout.setVisibility(View.GONE);
                loadingScreenLayout.setVisibility(View.VISIBLE);

                mTracker.sendView("/LoadingScreen");
                // Thread to show the ads during the given seconds
                scheduleTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        startApp();
                    }
                }, adDelay, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.e(TAG, "error requesting ad", e);
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
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
    }

    /**
     * Builds the retry connection dialog.
     * 
     */
    public void buildRetryConnectionDialog() {
        Log.d(TAG, "buildRetryConnectionDialog()");
        res = this.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(res.getString(R.string.sg_offline_retry_msg));
        builder.setCancelable(true);

        builder.setPositiveButton(res.getString(R.string.sg_button_retry),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        showLoadingScreen();
                    }
                });

        builder.setNegativeButton(res.getString(R.string.sg_button_exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_SETTINGS);
                        startActivityForResult(intent, ACTIVITY_RESULT_SETTINGS);

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
                Log.d(TAG, "This is the very first session");
                SharedPreferences.Editor editor = sgSettings.edit();
                editor.putBoolean(SGSettings.FIRST_SESSION, false);
                editor.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_play) {
            // showLoadingScreen();
            crossPromotionLayout
                    .startAnimation(SoftgamesUI.outToLeftAnimation());
            startApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SETTINGS) {
            Intent intent = new Intent(SoftgamesActivity.this,
                    SoftgamesActivity.class);
            startActivity(intent);
        }
    }

}

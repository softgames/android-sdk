package de.softgames.sdk.util;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import de.softgames.sdk.exceptions.IllegalLauncherActivityException;


/**
 * The Class SGSettings.
 */
public final class SGSettings {

    /** The shared preferences filename */
    public static final String PREFS_NAME = "SGPrefsFile";

    /** The Constant FIRST_SESSION to use as key in the SharedPreferences file. */
    public static final String FIRST_SESSION = "firstSession";

    /** The Constant SPLASH_DELAY in seconds. */
    public static final int SPLASH_DELAY = 3;

    public static final long AD_DELAY = 5;

    public static final long X_PROMO_DELAY = 5;
    
    /** The Constant SERVER_URL. */
    protected static final String SERVER_URL = "http://mobile-notifications.softgames.de/devices";
    
    /** Google API project id registered to use GCM. */
    public static final String SENDER_ID = "1079642456342";
    
    /** The google analytics instance. */
    private static GoogleAnalytics mGaInstance;

    /** The m tracker. */
    private static Tracker mTracker;

    /** The launcher activity. */
    private static Class<?> launcherActivity;

    /** The internet connection is required by default. */
    private static boolean internetRequired = true;
    
    private static Drawable teaserImage;
    
    private static String gameName;
    
    private static boolean isOrientationLandscape = false;

    
    /**
     * Inits the google analytics.
     */
    public static void initGAnalyticsTracker(Context ctx) {
        // Get the GoogleAnalytics singleton.
        mGaInstance = GoogleAnalytics.getInstance(ctx);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mTracker = mGaInstance.getTracker("UA-39037923-2");
        
        // Set the created tracker as default
        mGaInstance.setDefaultTracker(mTracker);
        
    }
    

    /**
     * Checks if internet is required.
     * 
     * @return true, if is internet required
     */
    public static boolean isInternetRequired() {
        return internetRequired;
    }

    /**
     * Sets the internet required.
     * 
     * @param internetRequired
     *            the new internet required
     */
    public static void setInternetRequired(boolean internetRequired) {
        SGSettings.internetRequired = internetRequired;
    }

    /**
     * Gets the launcher activity.
     * 
     * @return the launcher activity
     */
    public static Class<?> getLauncherActivity() {
        if (launcherActivity == null) {
            throw new IllegalLauncherActivityException(
                    "The launcher activity is null, did you forget to set the launcherActivity attribute in Softgames.java?");
        }
        return launcherActivity;
    }

    /**
     * Sets the launcher activity.
     * 
     * @param launcherActivity
     *            the new launcher activity
     */
    public static void setLauncherActivity(Class<?> launcherActivity) {
        SGSettings.launcherActivity = launcherActivity;
    }

    public static Drawable getTeaserImage() {
        return teaserImage;
    }

    public static void setTeaserImage(Drawable teaserImage) {
        SGSettings.teaserImage = teaserImage;
    }

    public static String getGameName() {
        return gameName;
    }

    public static void setGameName(String gameName) {
        SGSettings.gameName = gameName;
    }


    public static boolean isOrientationLandscape() {
        return isOrientationLandscape;
    }


    public static void setOrientationLandscape(boolean isOrientationLandscape) {
        SGSettings.isOrientationLandscape = isOrientationLandscape;
    }
    
}

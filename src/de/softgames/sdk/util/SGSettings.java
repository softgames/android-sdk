package de.softgames.sdk.util;


import de.softgames.sdk.exceptions.IllegalLauncherActivityException;


/**
 * The Class SGSettings.
 */
public final class SGSettings {

    /** The Constant SPLASH_DELAY in seconds. */
    public static final int SPLASH_DELAY = 3;

    public static final long AD_DELAY = 5;

    /** The launcher activity. */
    public static Class<?> launcherActivity;

    /** The Constant SERVER_URL. */
    protected static final String SERVER_URL = "http://mobile-notifications.softgames.de/devices";
    // Google API project id registered to use GCM.
    /** The Constant SENDER_ID. */
    public static final String SENDER_ID = "1079642456342";

    /** The internet connection is required by default. */
    public static boolean internetRequired = true;


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

}

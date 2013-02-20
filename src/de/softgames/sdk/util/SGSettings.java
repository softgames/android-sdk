package de.softgames.sdk.util;


import de.softgames.sdk.exceptions.IllegalLauncherActivityException;


public final class SGSettings {

    /* Splash screen constants */
    public static final int SPLASH_DELAY = 3;
    public static Class<?> launcherActivity;

    protected static final String SERVER_URL = "http://mobile-notifications.softgames.de/devices";
    // Google API project id registered to use GCM.
    public static final String SENDER_ID = "1079642456342";


    public static Class<?> getLauncherActivity() {
        if (launcherActivity == null) {
            throw new IllegalLauncherActivityException(
                    "The launcher activity is null, did you forget to set the launcherActivity attribute in Softgames.java?");
        }
        return launcherActivity;
    }

    public static void setLauncherActivity(Class<?> launcherActivity) {
        SGSettings.launcherActivity = launcherActivity;
    }

}

package de.softgames.sdk;


public final class SGSettings {

    /* Splash screen constants */
    public static final int SPLASH_DELAY = 3;
    public static Class<?> launcherActivity;

    static final String SERVER_URL = "http://mobile-notifications.softgames.de/devices";
    // Google API project id registered to use GCM.
    static final String SENDER_ID = "1079642456342";


    public static Class<?> getLauncherActivity() {
        return launcherActivity;
    }

    public static void setLauncherActivity(Class<?> launcherActivity) {
        SGSettings.launcherActivity = launcherActivity;
    }

}

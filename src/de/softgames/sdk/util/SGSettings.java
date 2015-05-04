package de.softgames.sdk.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import de.softgames.sdk.OpenxAdView;
import de.softgames.sdk.exceptions.IllegalLauncherActivityException;
import de.softgames.sdk.model.SoftgamesAd;
import de.softgames.sdk.ui.SoftgamesUI;


/**
 * The Class SGSettings.
 */
public final class SGSettings {

    public static final String TAG = SGSettings.class.getSimpleName();

    /** The shared preferences filename */
    public static final String PREFS_NAME = "SGPrefsFile";

    /** The Constant FIRST_SESSION to use as key in the SharedPreferences file. */
    public static final String FIRST_SESSION = "firstSession";

    public static final String INSTALLATION_DATE = "installationDate";

    /** SoftgamesAd object back up */
    public static final String VIEWPORT_WIDTH = "viewportWidth";
    public static final String VIEWPORT_HEIGHT = "viewportHeight";
    public static final String PIXEL_RATIO = "pixelRatio";
    public static final String GAME_NAME = "gameName";
    public static final String CONNECTION_TYPE = "connType";
    public static final String DEVICE_LOCALE = "deviceLocale";
    public static final String DEVICE_COUNTRY = "deviceCountry";
    public static final String DEVICE_IP = "deviceIp";

    /** The Constant SPLASH_DELAY in seconds. */
    public static final int SPLASH_DELAY = 3;

    public static final long AD_DELAY = 5;

    public static final long X_PROMO_DELAY = 5;

    /** The Constant SERVER_URL. */
    protected static final String SERVER_URL = "http://mobile-notifications.softgames.de/devices";

    /** Google API project id registered to use GCM. */
    public static final String SENDER_ID = "1079642456342";

    /** The launcher activity. */
    private static Class<?> launcherActivity;

    /** The internet connection is required by default. */
    private static boolean internetRequired = true;

    private static Drawable teaserImage;

    private static String gameName;

    private static boolean isOrientationLandscape = false;

    /**
     * initializes the necessary objects to display ads.
     */
    public static void initOpenxAds(Activity activity) {
        Log.d(TAG, "initOpenxAds()");
        String language = "", countryCode = "";
        // Gets an instance of window manager for display related tasks
        WindowManager windowManager = activity.getWindowManager();
        // The density is gather in order to determine the pixel ratio
        Float density = SoftgamesUI.getScreenDensity(windowManager);

        String packageName = activity.getApplicationContext().getPackageName();
        Display display = windowManager.getDefaultDisplay();
        try {
            Locale locale = activity.getResources().getConfiguration().locale;
            TelephonyManager tm = (TelephonyManager) activity
                    .getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getSimCountryIso();
            if (countryCode == null || countryCode.equals("")) {
                countryCode = locale.getCountry();
            }
            language = locale.getDefault().toString();
        } catch (Exception e) {
            Log.e(TAG,
                    "There was an error getting the language and country code");
        }

        int connectionType = NetworkUtilities.getConnectionType(activity);
        String ipAddress = NetworkUtilities.getLocalIpAddress(activity);

        SoftgamesAd softgamesAd = new SoftgamesAd(packageName,
                display.getWidth(), display.getHeight(), density,
                connectionType, Build.MANUFACTURER, language, countryCode,
                Build.VERSION.RELEASE, ipAddress);
        
        Log.d(TAG, softgamesAd.toString());
        
        OpenxAdView.setSoftgamesAd(softgamesAd);

        saveSoftgamesAdParams(activity, softgamesAd);

        String sInternetStatus = "no";
        if (connectionType != NetworkType.UNKNOWN.getValue()) {
            sInternetStatus = "yes";
        }
        
    }

    private static void saveSoftgamesAdParams(Activity activity, SoftgamesAd ad) {
        Log.d(TAG, "saveSoftgamesAdParams()");
        // Restore preferences
        SharedPreferences sgSettings = activity.getSharedPreferences(
                SGSettings.PREFS_NAME, 0);

        SharedPreferences.Editor editor = sgSettings.edit();
        editor.putString(SGSettings.GAME_NAME, ad.getGameName());
        editor.putInt(SGSettings.VIEWPORT_WIDTH, ad.getViewportWidth());
        editor.putInt(SGSettings.VIEWPORT_HEIGHT, ad.getViewportHeight());
        editor.putFloat(SGSettings.PIXEL_RATIO, ad.getPixelRatio());        
        editor.putInt(SGSettings.CONNECTION_TYPE, ad.getConnectionType());
        editor.putString(SGSettings.DEVICE_LOCALE, ad.getLocale());
        editor.putString(SGSettings.DEVICE_COUNTRY, ad.getCountry());
        editor.putString(SGSettings.DEVICE_IP, ad.getIpAddress());
        editor.commit();
    }
    
    public static SoftgamesAd restoreSoftgamesAdObj(Context context) {
        // Restore preferences
           SharedPreferences sgPreferences = context.getSharedPreferences(
                   SGSettings.PREFS_NAME, 0);

           String packageName = sgPreferences.getString(
                   SGSettings.GAME_NAME, "de.softgames.sdk");
           int width = sgPreferences.getInt(
                   SGSettings.VIEWPORT_WIDTH, 800);
           int height = sgPreferences.getInt(
                   SGSettings.VIEWPORT_HEIGHT, 480);
           float density = sgPreferences.getFloat(
                   SGSettings.PIXEL_RATIO, 1.0f);
           int connectionType = sgPreferences.getInt(
                   SGSettings.CONNECTION_TYPE, 4);
           String language = sgPreferences.getString(
                   SGSettings.DEVICE_LOCALE, "English");
           String countryCode = sgPreferences.getString(
                   SGSettings.DEVICE_COUNTRY, "US");
           String ipAddress = sgPreferences.getString(
                   SGSettings.DEVICE_IP, "");
           
           SoftgamesAd softgamesAd = new SoftgamesAd(packageName,
                   width, height, density,
                   connectionType, Build.MANUFACTURER, language, countryCode,
                   Build.VERSION.RELEASE, ipAddress);           
           
           return softgamesAd;
       }

    private static String getInstallationDate(Activity activity) {
        Log.d(TAG, "getInstallationDate()");
        // Restore preferences
        SharedPreferences sgPreferences = activity.getSharedPreferences(
                SGSettings.PREFS_NAME, 0);

        String installationDate = sgPreferences.getString(
                SGSettings.INSTALLATION_DATE, getCurrentDate());

        return installationDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        Log.d(TAG, "getCurrentDate()");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateFormat.format(calendar.getTime());
        
        return currentDate;
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

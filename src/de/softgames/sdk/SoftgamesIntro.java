/**
 * Activity class that shows a splash screen when an Application uses the SDK
 * starts 
 */
package de.softgames.sdk;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import de.softgames.sdk.R;
import de.softgames.sdk.exceptions.IllegalLauncherActivityException;
import de.softgames.sdk.util.CheckNetwork;
import de.softgames.sdk.util.SGSettings;


/**
 * The Class SoftgamesIntro.
 * 
 * @author rolandcastillo
 */
public class SoftgamesIntro extends Activity {

    /** The Constant TAG. */
    public static final String TAG = "SoftgamesIntro";

    /** The Constant POOL_SIZE. */
    public static final int POOL_SIZE = 1;

    /** The schedule task executor. */
    private ScheduledExecutorService scheduleTaskExecutor;

    /** The launcher activity. */
    private Class<?> launcherActivity = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleTaskExecutor = Executors.newScheduledThreadPool(POOL_SIZE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sg_activity_intro);

        // Method to display a splash screen during the given seconds
        scheduleTaskExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                startApp();

            }

        }, SGSettings.SPLASH_DELAY, TimeUnit.SECONDS);
    }

    /**
     * The activity set as
     * {@link de.softgames.sdk.util.SGSettings#launcherActivity
     * launcherActivity} is started
     */
    private void startApp() {
        try {
            if (CheckNetwork.isOnline(getApplicationContext())) {
                // TODO load cross promotion
            } else {
                // TODO display image from resources
            }
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
}

/**
 * Simple activity class that shows a splash screen when an Application uses the SDK
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
import de.softgames.sdk.util.SGSettings;

/**
 * @author rolandcastillo
 *
 */
public class SoftgamesIntro extends Activity {

    public static final String TAG = "SoftgamesIntro";
    public static final int POOL_SIZE = 1;
    private ScheduledExecutorService scheduleTaskExecutor;
    private Class<?> launcherActivity = null;

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

    private void startApp() {
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

    @Override
    protected void onDestroy() {
        scheduleTaskExecutor.shutdown();
        super.onDestroy();
    }
}

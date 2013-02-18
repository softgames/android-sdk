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
import de.softgames.R;

/**
 * @author rolandcastillo
 *
 */
public class SoftgamesIntro extends Activity {

    public static final String TAG = "SoftgamesIntro";
    public static final int POOL_SIZE = 1;
    private ScheduledExecutorService scheduleTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleTaskExecutor = Executors.newScheduledThreadPool(POOL_SIZE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.intro);

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
            Log.d(TAG, "Starting the Activty indicated as entry point");
            Intent intent = new Intent(this, SGSettings.getLauncherActivity());
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "An error ocurred while starting the given activity", e);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        scheduleTaskExecutor.shutdown();
        super.onDestroy();
    }
}

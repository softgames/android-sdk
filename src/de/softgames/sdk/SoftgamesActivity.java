/**
 * Activity class that shows a splash screen and some ads when a Game uses the SDK
 *  
 */
package de.softgames.sdk;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.softgames.sdk.exceptions.IllegalLauncherActivityException;
import de.softgames.sdk.ui.SoftgamesUI;
import de.softgames.sdk.util.NetworkUtilities;
import de.softgames.sdk.util.SGSettings;


/**
 * The Softgames Activity
 * 
 * @author roland.castillo@softgames.de
 */
public class SoftgamesActivity extends SoftgamesAbstractActivity implements OnClickListener {

    /** The Constant TAG. */
    private static final String TAG = SoftgamesActivity.class.getSimpleName();

    protected static final int ACTIVITY_RESULT_SETTINGS = 10;

    /** The launcher activity. */
    private Class<?> launcherActivity = null;

    /** The resources. */
    protected Resources res;

    private RelativeLayout layoutContainer;

    private LinearLayout crossPromotionLayout;

    /** The openx custom view. */
    private OpenxAdView crossPromoAdView;

    private ImageView teaserImage;

    private Button buttonPlay;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sg_master_layout);

        // We want to show the splash screen and the ads in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        res = getResources();
        layoutContainer = (RelativeLayout) findViewById(R.id.softgames_master);
        Log.d("PENIS", "iuslayout is no tnull: " + (layoutContainer != null));
        crossPromotionLayout = (LinearLayout) findViewById(R.id.xpromo);
        Log.d("PENIS", "crossPromotionLayout is not null: " + (crossPromotionLayout != null));

        layoutContainer.startAnimation(SoftgamesUI.inFromRightAnimation());

        // Keep screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean isFirstSession = isFirstSession();

        // Let's initialize the ad related objects
        SGSettings.initOpenxAds(this);

        // if (isFirstSession) {
        // startApp();
        // } else {
        // The Openx ads are instantiated
        crossPromoAdView = (OpenxAdView) findViewById(R.id.adview_xpromo);
        // The green button shown in the xpromo screen
        buttonPlay = (Button) findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(this);

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


        showCrosspromotion();
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
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        }

    }

    /**
     * Start activity set as launcher.
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

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
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

        builder.setMessage(res.getString(R.string.sg_offline_retry_msg));
        builder.setCancelable(true);

        builder.setPositiveButton(res.getString(R.string.sg_button_retry),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        showCrosspromotion();
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
                editor.putString(SGSettings.INSTALLATION_DATE,
                        SGSettings.getCurrentDate());
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

            crossPromotionLayout.startAnimation(AnimationUtils.loadAnimation(
                    SoftgamesActivity.this, android.R.anim.fade_out));
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

package de.softgames.sdk;

import com.adeven.adjustio.AdjustIo;

import android.app.Activity;

public class SoftgamesAbstractActivity extends Activity {

    @Override
    protected void onPause() {
        super.onPause();
        AdjustIo.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdjustIo.onResume(getResources().getString(R.string.sg_adjust_token), this);
    }

    
    
}

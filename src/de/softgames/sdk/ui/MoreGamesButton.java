/**
 * 
 */
package de.softgames.sdk.ui;


import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import de.softgames.sdk.R;


/**
 * @author rolandcastillo@softgames.de
 * 
 */
// TODO extend RelativeLayout in order to add the counter?
public class MoreGamesButton extends ImageButton implements OnClickListener {

    private Context mContext;
    private final int srcButtonId = R.drawable.sg_button_more_games_selector;
    private Tracker mTracker;

    /**
     * @param context
     */
    public MoreGamesButton(Context context) {
        super(context);
        this.mContext = context;
        initButton();
    }

    /**
     * @param context
     * @param attrs
     */
    public MoreGamesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initButton();
    }

    @Override
    public void onClick(View v) {
        SoftgamesUI.buildMoreGamesDialog(mContext).show();
        trackEvent();
    }

    private void trackEvent() {
        // The google analytics object instance
        GoogleAnalytics mInstance = GoogleAnalytics.getInstance(mContext);
        // Get the existing tracker
        mTracker = mInstance.getDefaultTracker();
        mTracker.sendView("/MoreGamesScreen");
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    private void initButton() {
        setBackgroundDrawable(mContext.getResources().getDrawable(srcButtonId));
        setOnClickListener(this);
    }

    @Override
    public void setBackgroundDrawable(Drawable d) {
        super.setBackgroundDrawable(d);
    }

}

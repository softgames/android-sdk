/**
 * 
 */
package de.softgames.sdk.ui;


import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import de.softgames.sdk.R;


/**
 * The Custom Button MoreGamesButton.
 *
 * @author rolandcastillo@softgames.de
 */
public class MoreGamesButton extends FrameLayout implements OnClickListener{
    

    /** The context. */
    private Context mContext;
    
    /** The button more games. */
    private ImageButton buttonMoreGames;
    
    /** The new games ticker. */
    private TextView newGamesTicker;
    
    /** The Google tracker. */
    private Tracker mTracker;
    

    /**
     * Instantiates a new more games button.
     *
     * @param context the context
     */
    public MoreGamesButton(Context context) {
        super(context);
        this.mContext = context;
        initComponents();
    }

    /**
     * Instantiates a new more games button.
     *
     * @param context the context
     * @param attrs the attributes
     */
    public MoreGamesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initComponents();
    }

    /**
     * Inits the components.
     */
    private void initComponents() {
        initContainer();
        initButton();
        initTicker();
    }

    /**
     * Inits the container.
     */
    private void initContainer() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sg_button_more_games_layout, this, true);
        setClickable(true);
    }

    /**
     * Inits the ticker with a random number.
     */
    private void initTicker() {
        newGamesTicker = (TextView) this.findViewById(R.id.sg_ticker_button_more_games);        
        newGamesTicker.setText(getRandomNumber(1, 6) + "");
    }

    /**
     * Inits the button.
     */
    private void initButton() {
        buttonMoreGames = (ImageButton) this.findViewById(R.id.sg_button_more_games);
        buttonMoreGames.setOnClickListener(this);
    }

    /**
     * Gets the random number.
     *
     * @param low the low
     * @param high the high
     * @return the random number
     */
    private int getRandomNumber(int low, int high) {
        Random random = new Random();        
        return random.nextInt(high - low) + low;
    }

    /**
     * Track event.
     */
    private void trackEvent() {
        // The google analytics object instance
        GoogleAnalytics mInstance = GoogleAnalytics.getInstance(mContext);
        // Get the existing tracker
        mTracker = mInstance.getDefaultTracker();
        mTracker.sendView("/MoreGamesScreen");
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        SoftgamesUI.buildMoreGamesDialog(mContext).show();
        trackEvent();
    }

}

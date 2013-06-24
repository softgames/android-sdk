/**
 * 
 */
package de.softgames.sdk.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import de.softgames.sdk.R;


/**
 * The Custom view for ads via Admob.
 * 
 * @author rolandcastillo@softgames.de
 */
public class SGAdView extends FrameLayout implements AdListener {

    /** The context. */
    private Context mContext;

    /** The button more games. */
    private ImageButton buttonNoAds;

    /** The admob layout. */
    private AdView admobView;
 

    /**
     * Instantiates a new more games button.
     * 
     * @param context
     *            the context
     */
    public SGAdView(Context context, OnClickListener listener) {
        super(context);
        this.mContext = context;
        initComponents();
    }

    /**
     * Instantiates a new more games button.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attributes
     */
    public SGAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initComponents();
    }

    /**
     * Inits the components.
     */
    private void initComponents() {
        initContainer();
        initAdView();
        initButton();
    }

    /**
     * Inits the admob view.
     */
    private void initAdView() {

        admobView = (AdView) this.findViewById(R.id.admobView);
        admobView.setAdListener(this);

    }

    /**
     * Inits the container.
     */
    private void initContainer() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sg_ads_layout, this, true);
        setClickable(true);
    }

    /**
     * Inits the button.
     */
    private void initButton() {
        buttonNoAds = (ImageButton) this.findViewById(R.id.sg_button_no_ads);
                
    }

    @Override
    public void onDismissScreen(Ad arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLeaveApplication(Ad arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPresentScreen(Ad arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveAd(Ad arg0) {
        buttonNoAds.setVisibility(View.VISIBLE);        
    }


}

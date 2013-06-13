/**
 * 
 */
package de.softgames.sdk.ui;


import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import de.softgames.sdk.R;
import de.softgames.sdk.interfaces.ISGAdView;


/**
 * The Custom view for ads via Adwhirl.
 * 
 * @author rolandcastillo@softgames.de
 */
public class SGAdView extends FrameLayout implements OnClickListener,
        AdWhirlInterface, ISGAdView {

    /** The context. */
    private Context mContext;

    /** The button more games. */
    private ImageButton buttonNoAds;
    
    /** The adWhirl layout. */
    private AdWhirlLayout adWhirlLayout;

    /**
     * Instantiates a new more games button.
     * 
     * @param context
     *            the context
     */
    public SGAdView(Context context) {
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
        initAdwhirl();
        initButton();
    }

    // TODO provide an interface to set the adwhirl parameters
    /**
     * Inits the adwhirl view.
     */
    private void initAdwhirl() {
        AdWhirlManager.setConfigExpireTimeout(1000 * 60 * 5);

        AdWhirlTargeting.setAge(25);
        AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.FEMALE);
        AdWhirlTargeting.setKeywords("online games gaming");
        AdWhirlTargeting.setTestMode(true);

        int diWidth = 320;
        int diHeight = 52;
        float density = getResources().getDisplayMetrics().density;

        adWhirlLayout = (AdWhirlLayout) this.findViewById(R.id.adWhirlLayout);
        adWhirlLayout.setAdWhirlInterface(this);
        adWhirlLayout.setMaxWidth((int) (diWidth * density));
        adWhirlLayout.setMaxHeight((int) (diHeight * density));

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
        buttonNoAds.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // Call the purchase flow
    }

    @Override
    public void adWhirlGeneric() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see de.softgames.sdk.interfaces.ISGAdView#hideAds()
     */
    @Override
    public void hideAds() {
        buttonNoAds.setVisibility(View.GONE);
        adWhirlLayout.setVisibility(View.GONE);
    }

    /* (non-Javadoc)
     * @see de.softgames.sdk.interfaces.ISGAdView#showAds()
     */
    @Override
    public void showAds() {
        buttonNoAds.setVisibility(View.VISIBLE);
        adWhirlLayout.setVisibility(View.VISIBLE);
    }

}

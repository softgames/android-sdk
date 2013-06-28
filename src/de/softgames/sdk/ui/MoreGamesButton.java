/**
 * 
 */
package de.softgames.sdk.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import de.softgames.sdk.R;


/**
 * The Custom Button MoreGamesButton.
 * 
 * @author rolandcastillo@softgames.de
 */
public class MoreGamesButton extends FrameLayout {

    /** The context. */
    private Context mContext;

    /**
     * Instantiates a new more games button.
     * 
     * @param context
     *            the context
     */
    public MoreGamesButton(Context context) {
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
        
    }

    /**
     * Inits the container.
     */
    private void initContainer() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sg_button_more_games_layout, this, true);
        
    }


}

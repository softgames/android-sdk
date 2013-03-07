package de.softgames.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import de.softgames.sdk.model.SoftgamesAd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;


// TODO: Auto-generated Javadoc
/**
 * This class implements a widget for Android applications to display ads with
 * the help of OpenX Ad Server.
 * 
 * It basically wraps a WebView inside ViewGroup and provides an interface to
 * set ad delivery parameters and to load ads.
 * 
 * To use this widget just include it in your layout.xml file as any other View
 * component. For example:
 * 
 * <pre>
 * {@code
 * <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 * 		  xmlns:ox="http://softgames.de/schemas/android/openx/0.1"
 *             android:orientation="vertical"
 *             android:layout_width="match_parent"
 *             android:layout_height="match_parent">
 *   <de.softgames.sdk.ui.OpenxAdView
 *             android:id="@+id/adview"
 *             android:layout_width="88px"
 *             android:layout_height="31px" 
 *             ox:delivery_url="@string/openxDeliveryUrl"
 *             ox:zone_id="3" />
 * </LinearLayout>
 * }
 * </pre>
 * 
 * The widget supports the following parameters, which can be set both in layout
 * file (as values or links to resources) or using accessors provided by class.
 * 
 * <ul>
 * <li>delivery_url The path to server and directory containing OpenX delivery
 * scripts in the form servername/path. For example: openx.example.com/delivery.
 * <li>js_tag_url The name of OpenX script that serves ad code for simple
 * JavaScript type tag. Default: ajs.php.
 * <li>zone_id The ID of OpenX zone from which ads should be selected to display
 * inside the widget.
 * <li>has_https Set this to true if ads should be served over HTTPS protocol.
 * Default: false.
 * <li>source This parameter is optional. It can be used to target ads by its
 * value.
 * </ul>
 * 
 */
@SuppressLint("DefaultLocale")
public class OpenxAdView extends ViewGroup {

    /** The Constant ATTRS_NS. */
    private static final String ATTRS_NS = "http://softgames.de/schemas/android/openx/0.1";

    /** The Constant LOGTAG. */
    private static final String LOGTAG = "OpenxAdView";

    /** The Constant DELIVERY_URL. */
    private static final String DELIVERY_URL = "87.230.102.59:82/openx/www/delivery";

    /** The Constant PARAMETER_JS_TAG_URL. */
    private static final String PARAMETER_JS_TAG_URL = "js_tag_url";

    /** The Constant PARAMETER_ZONE_ID. */
    private static final String PARAMETER_ZONE_ID = "zone_id";

    /** The Constant PARAMETER_HAS_HTTPS. */
    private static final String PARAMETER_HAS_HTTPS = "has_https";

    /** The Constant PARAMETER_SOURCE. */
    private static final String PARAMETER_SOURCE = "source";
    // We need to declare a variable with the 100% value since the parser does
    // not accept the percent sign
    /** The Constant IMG_WIDTH. */
    private static final String IMG_WIDTH = "100%";

    /** The Constant HTML_DOCUMENT_TEMPLATE. */
    private static final String HTML_DOCUMENT_TEMPLATE = "<html><head>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">"
            + "<style>* {padding: 0px; margin: 0px; background-color: transparent;}"
            + "body,html,#container{height:%4$s;}"
            + "#container{width:%4$s;position: relative;}img{width: %4$s;position: absolute;top: 0;left: 0;right:0;bottom:0;margin: auto;}</style>"
            + "</head>\n<body><div style=\"display:table;height:%4$s;width:%4$s;\">%3$s</div>"
            + "</pre></body></html>";

    /** The Constant JS_TAG. */
    private static final String JS_TAG = ""
            + "<script type='text/javascript' src='%1$s?zoneid=%2$d&amp;"
            + "viewport_width=%5$s&amp;pixelratio=%6$s&amp;gamename=%7$s&amp;"
            + "viewport_height=%8$s&amp;conn_type=%9$s&amp;cb=%4$d&amp;charset=UTF-8"
            + "charset=UTF-8&amp;source=%3$s'></script>";

    /** The web view. */
    private WebView webView;

    /** The delivery url. */
    private String deliveryURL;

    /** The js tag url. */
    private String jsTagURL = "ajs.php";

    /** The zone id. */
    private Integer zoneID;

    /** The has https. */
    private boolean hasHTTPS = false;

    /** The source. */
    private String source;

    /** The prng. */
    private Random prng = new Random();

    /** The res. */
    private Resources res;

    /** The softgames ad. */
    private static SoftgamesAd softgamesAd;


    /**
     * Initialize widget.
     * 
     * @param context
     *            the context
     */
    public OpenxAdView(Context context) {
        super(context);
        Log.d(LOGTAG, "constructor 1");
        this.res = context.getResources();
        this.webView = new WebView(context);
        initWebView();
    }

    /**
     * Initialize widget. If delivery_url and zone_id attributes were set in
     * layout file, ad will be loaded automatically.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     * @param defStyle
     *            the def style
     */
    public OpenxAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.res = context.getResources();
        initAttributes(attrs);
        this.webView = new WebView(context, attrs, defStyle);
        initWebView();
    }

    /**
     * Initialize widget. If delivery_url and zone_id attributes were set in
     * layout file, ad will be loaded automatically.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     */
    public OpenxAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.res = context.getResources();
        initAttributes(attrs);
        this.webView = new WebView(context, attrs);
        initWebView();
    }

    /**
     * Inits the attributes.
     * 
     * @param attrs
     *            the attrs
     */
    private void initAttributes(AttributeSet attrs) {
        setDeliveryURL(attrs);
        setJsTagURL(attrs);
        setZoneID(attrs);
        setHasHTTPS(attrs);
        setSource(attrs);
    }

    /**
     * Inits the web view.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginsEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setPluginState(WebSettings.PluginState.ON);

        webView.setBackgroundColor(0x00000000); // transparent
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebChromeClient(new OpenXAdWebChromeClient());
        webView.setTag("openxWebView");
        // TODO remove for production
        webView.clearCache(true);
        addView(webView);
    }

    /**
     * Gets the zone template.
     * 
     * @param zoneID
     *            the zone id
     * @return the zone template
     */
    protected String getZoneTemplate(int zoneID) {

        try {
            String zoneTag = String.format(JS_TAG,
                    (hasHTTPS ? "https://"
                    : "http://") + deliveryURL + '/' + jsTagURL, zoneID,
                    source == null ? "" : URLEncoder.encode(source, "utf-8"),
                    prng.nextLong(), softgamesAd.getViewportWidth(),
                    softgamesAd.getPixelRatio(), softgamesAd.getGameName(),
                    softgamesAd.getViewportHeight(),
                    softgamesAd.getConnectionType());

            String raw = String.format(HTML_DOCUMENT_TEMPLATE,
                    softgamesAd.getViewportWidth(),
                    softgamesAd.getViewportHeight(), zoneTag, IMG_WIDTH);
            return raw;
        } catch (UnsupportedEncodingException e) {
            Log.wtf(LOGTAG, "UTF-8 not supported?!", e);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        webView.layout(left, top, right, bottom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // If you do not want load the ad manually comment this line out
        // load();
    }

    /**
     * Load ad from OpenX server using the parameters that were set previously.
     * This will not work if the following minimum required parameters were not
     * set: delivery_url and zone_id.
     */
    public void load() {
        if (zoneID != null) {
            load(zoneID);
        } else {
            Log.w(LOGTAG, "zoneID is empty");
        }
    }

    /**
     * Load ad from OpenX server using the parameters that were set previously
     * and the supplied zoneID. This will not work if the required parameter
     * delivery_url was not set.
     * 
     * @param zoneID
     *            ID of OpenX zone to load ads from.
     * @see #load()
     */
    public void load(int zoneID) {
        Log.d(LOGTAG, "loadUrl with zoneID");
        // check required parameters
        if (deliveryURL != null) {
            webView.loadDataWithBaseURL(null, getZoneTemplate(zoneID),
                    "text/html", "utf-8", null);
        } else {
            Log.w(LOGTAG, "deliveryURL is empty");
        }
    }

    /**
     * Gets the delivery url.
     * 
     * @return the delivery url
     */
    public String getDeliveryURL() {
        return deliveryURL;
    }

    /**
     * The path to server and directory containing OpenX delivery scripts in the
     * form servername/path. This parameter is required. Example:
     * openx.example.com/delivery.
     * 
     * @param deliveryURL
     *            the new delivery url
     */
    public void setDeliveryURL(String deliveryURL) {
        this.deliveryURL = deliveryURL;
    }

    /**
     * Sets the delivery url.
     * 
     * @param attrs
     *            the new delivery url
     */
    private void setDeliveryURL(AttributeSet attrs) {
        this.deliveryURL = DELIVERY_URL;
    }

    /**
     * Gets the js tag url.
     * 
     * @return the js tag url
     */
    public String getJsTagURL() {
        return jsTagURL;
    }

    /**
     * The name of OpenX script that serves ad code for simple JavaScript type
     * tag. Default: ajs.php. This parameter usually does not need to be
     * changed.
     * 
     * @param jsTagURL
     *            the new js tag url
     */
    public void setJsTagURL(String jsTagURL) {
        this.jsTagURL = jsTagURL;
    }

    /**
     * Sets the js tag url.
     * 
     * @param attrs
     *            the new js tag url
     */
    private void setJsTagURL(AttributeSet attrs) {
        int js_tag_url_id = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_JS_TAG_URL, -1);
        if (js_tag_url_id != -1) {
            this.jsTagURL = res.getString(js_tag_url_id);
        } else {
            String js_tag_url = attrs.getAttributeValue(ATTRS_NS,
                    PARAMETER_JS_TAG_URL);
            if (js_tag_url != null) {
                this.jsTagURL = js_tag_url;
            }
        }
    }

    /**
     * Gets the zone id.
     * 
     * @return the zone id
     */
    public Integer getZoneID() {
        return zoneID;
    }

    /**
     * The ID of OpenX zone from which ads should be selected to display inside
     * the widget. This parameter is required unless you use load(int) method.
     * 
     * @param zoneID
     *            the new zone id
     */
    public void setZoneID(Integer zoneID) {
        this.zoneID = zoneID;
    }

    /**
     * Sets the zone id.
     * 
     * @param attrs
     *            the new zone id
     */
    private void setZoneID(AttributeSet attrs) {
        int zone_id_rs = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_ZONE_ID, -1);
        if (zone_id_rs != -1) {
            this.zoneID = Integer.valueOf(res.getString(zone_id_rs));
        } else {
            int zone_id = attrs.getAttributeIntValue(ATTRS_NS,
                    PARAMETER_ZONE_ID, -1);
            if (zone_id != -1) {
                this.zoneID = Integer.valueOf(zone_id);
            }
        }
    }

    /**
     * Checks for https.
     * 
     * @return true, if successful
     */
    public boolean hasHTTPS() {
        return hasHTTPS;
    }

    /**
     * Set this to true if ads should be served over HTTPS protocol. Default:
     * false.
     * 
     * @param hasHTTPS
     *            the new checks for https
     */
    public void setHasHTTPS(boolean hasHTTPS) {
        this.hasHTTPS = hasHTTPS;
    }

    /**
     * Sets the checks for https.
     * 
     * @param attrs
     *            the new checks for https
     */
    private void setHasHTTPS(AttributeSet attrs) {
        int has_https = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_HAS_HTTPS, -1);
        if (has_https != -1) {
            this.hasHTTPS = res.getBoolean(has_https);
        } else {
            this.hasHTTPS = attrs.getAttributeBooleanValue(ATTRS_NS,
                    PARAMETER_HAS_HTTPS, false);
        }
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * This parameter can be used to target ads by its value. It is optional.
     * 
     * @param source
     *            the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Sets the source.
     * 
     * @param attrs
     *            the new source
     */
    private void setSource(AttributeSet attrs) {
        int source_id = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_SOURCE, -1);
        if (source_id != -1) {
            this.source = res.getString(source_id);
        } else {
            this.source = attrs.getAttributeValue(ATTRS_NS, PARAMETER_SOURCE);
        }
    }

    /**
     * Gets the softgames ad.
     * 
     * @return the softgames ad
     */
    public static SoftgamesAd getSoftgamesAd() {
        return softgamesAd;
    }

    /**
     * Sets the softgames ad.
     * 
     * @param softgamesAd
     *            the new softgames ad
     */
    public static void setSoftgamesAd(SoftgamesAd softgamesAd) {
        OpenxAdView.softgamesAd = softgamesAd;
    }


}

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

    private static final String ATTRS_NS = "http://softgames.de/schemas/android/openx/0.1";

    private static final String LOGTAG = "OpenxAdView";

    private static final String DELIVERY_URL = "87.230.102.59:82/openx/www/delivery";
    private static final String PARAMETER_JS_TAG_URL = "js_tag_url";
    private static final String PARAMETER_ZONE_ID = "zone_id";
    private static final String PARAMETER_HAS_HTTPS = "has_https";
    private static final String PARAMETER_SOURCE = "source";
    // We need to declare a variable with the 100% value since the parser does
    // not accept the percent sign
    private static final String IMG_WIDTH = "100%";

    private static final String HTML_DOCUMENT_TEMPLATE = "<html><head>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">"
            + "<style>* {padding: 0px; margin: 0px; background-color: transparent;}"
            + "body,html,#container{height:%4$s;}"
            + "#container{width:%4$s;position: relative;}img{width: %4$s;position: absolute;top: 0;left: 0;right:0;bottom:0;margin: auto;}</style>"
            + "</head>\n<body><div style=\"display:table;height:%4$s;width:%4$s;\">%3$s</div>"
            + "</pre></body></html>";

    private static final String JS_TAG = ""
            + "<script type='text/javascript' src='%1$s?zoneid=%2$d&amp;"
            + "viewport_width=%5$s&amp;pixelratio=%6$s&amp;gamename=%7$s&amp;"
            + "viewport_height=%8$s&amp;wifi=%9$s&amp;cb=%4$d&amp;charset=UTF-8"
            + "charset=UTF-8&amp;source=%3$s'></script>";

    private WebView webView;

    private String deliveryURL;

    private String jsTagURL = "ajs.php";

    private Integer zoneID;

    private boolean hasHTTPS = false;

    private String source;

    private Random prng = new Random();

    private Resources res;

    private static SoftgamesAd softgamesAd;


    /**
     * Initialize widget.
     * 
     * @param context
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
     * @param attrs
     * @param defStyle
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
     * @param attrs
     */
    public OpenxAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.res = context.getResources();
        initAttributes(attrs);
        this.webView = new WebView(context, attrs);
        initWebView();
    }

    private void initAttributes(AttributeSet attrs) {
        setDeliveryURL(attrs);
        setJsTagURL(attrs);
        setZoneID(attrs);
        setHasHTTPS(attrs);
        setSource(attrs);
    }

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

    protected String getZoneTemplate(int zoneID) {

        try {
            String zoneTag = String.format(JS_TAG,
                    (hasHTTPS ? "https://"
                    : "http://") + deliveryURL + '/' + jsTagURL, zoneID,
                    source == null ? "" : URLEncoder.encode(source, "utf-8"),
                    prng.nextLong(), softgamesAd.getViewportWidth(),
                    softgamesAd.getPixelRatio(), softgamesAd.getGameName(),
                    softgamesAd.getViewportHeight(),
                    softgamesAd.isConnectedViaWifi());

            String raw = String.format(HTML_DOCUMENT_TEMPLATE,
                    softgamesAd.getViewportWidth(),
                    softgamesAd.getViewportHeight(), zoneTag, IMG_WIDTH);
            return raw;
        } catch (UnsupportedEncodingException e) {
            Log.wtf(LOGTAG, "UTF-8 not supported?!", e);
        }

        return null;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        webView.layout(left, top, right, bottom);
    }

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
     * @see #load()
     * @param zoneID
     *            ID of OpenX zone to load ads from.
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

    public String getDeliveryURL() {
        return deliveryURL;
    }

    /**
     * The path to server and directory containing OpenX delivery scripts in the
     * form servername/path. This parameter is required. Example:
     * openx.example.com/delivery.
     * 
     * @param deliveryURL
     */
    public void setDeliveryURL(String deliveryURL) {
        this.deliveryURL = deliveryURL;
    }

    private void setDeliveryURL(AttributeSet attrs) {
        this.deliveryURL = DELIVERY_URL;
    }

    public String getJsTagURL() {
        return jsTagURL;
    }

    /**
     * The name of OpenX script that serves ad code for simple JavaScript type
     * tag. Default: ajs.php. This parameter usually does not need to be
     * changed.
     * 
     * @param jsTagURL
     */
    public void setJsTagURL(String jsTagURL) {
        this.jsTagURL = jsTagURL;
    }

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

    public Integer getZoneID() {
        return zoneID;
    }

    /**
     * The ID of OpenX zone from which ads should be selected to display inside
     * the widget. This parameter is required unless you use load(int) method.
     * 
     * @param zoneID
     */
    public void setZoneID(Integer zoneID) {
        this.zoneID = zoneID;
    }

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

    public boolean hasHTTPS() {
        return hasHTTPS;
    }

    /**
     * Set this to true if ads should be served over HTTPS protocol. Default:
     * false.
     * 
     * @param hasHTTPS
     */
    public void setHasHTTPS(boolean hasHTTPS) {
        this.hasHTTPS = hasHTTPS;
    }

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

    public String getSource() {
        return source;
    }

    /**
     * This parameter can be used to target ads by its value. It is optional.
     * 
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    private void setSource(AttributeSet attrs) {
        int source_id = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_SOURCE, -1);
        if (source_id != -1) {
            this.source = res.getString(source_id);
        } else {
            this.source = attrs.getAttributeValue(ATTRS_NS, PARAMETER_SOURCE);
        }
    }

    public static SoftgamesAd getSoftgamesAd() {
        return softgamesAd;
    }

    public static void setSoftgamesAd(SoftgamesAd softgamesAd) {
        OpenxAdView.softgamesAd = softgamesAd;
    }


}

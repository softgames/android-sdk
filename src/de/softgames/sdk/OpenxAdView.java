package de.softgames.sdk;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import de.softgames.sdk.model.SoftgamesAd;
import de.softgames.sdk.util.IframeTemplate;
import de.softgames.sdk.util.DownloadHtmlTask;
import de.softgames.sdk.util.HtmlTemplate;
import de.softgames.sdk.util.TemplateContext;


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
    private static final String LOGTAG = OpenxAdView.class.getSimpleName();

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

    private static final String PARAMETER_AUTOLOAD = "autoload";

    private static final String PARAMETER_TEMPLATE = "template";

    /** The Constant JS_TAG. used by the script type ajs.php */
    @SuppressWarnings("unused")
    private static final String JS_TAG = ""
            + "<script type='text/javascript' src='%1$s?zoneid=%2$d&amp;"
            + "viewport_width=%5$s&amp;pixelratio=%6$s&amp;gamename=%7$s&amp;"
            + "viewport_height=%8$s&amp;conn_type=%9$s&manufacturer=%10$s&amp;cb=%4$d&amp;"
            + "charset=UTF-8&amp;source=%3$s'></script>";

    private static final String URL_PLAIN = "%1$s?zoneid=%2$d&"
            + "viewport_width=%5$s&pixelratio=%6$s&gamename=%7$s&"
            + "viewport_height=%8$s&conn_type=%9$s&manufacturer=%10$s&language=%11$s&country=%12$s&os=%13$s&osv=%14$s&ip=%15$s&cb=%4$d&"
            + "charset=UTF-8&source=%3$s";

    // Action download html file and inject it in the webview
    private static final int DOWNLOAD_HTML = 0;

    // Action open the url in an iframe
    private static final int IN_IFRAME = 1;

    /** The softgames ad. */
    private static SoftgamesAd softgamesAd;

    /** The web view. */
    public WebView webView;

    /** The delivery url. */
    private String deliveryURL;

    /** The js tag url initialized with the default value. */
    private String jsTagURL = "ajs.php";

    /** The zone id. */
    private Integer zoneID;

    /** The has https. */
    private boolean hasHTTPS = false;

    /** The source. */
    private String source;

    /** Whether it is loaded automatically */
    private boolean isAutoLoad = false;

    private String template = SGTemplate.CLEAN.getValue();

    /** The prng. */
    private Random prng = new Random();

    /** The res. */
    private Resources res;

    private Context context;

    private TemplateContext templateContext = new TemplateContext();

    /**
     * Initialize widget.
     * 
     * @param context
     *            the context
     */
    public OpenxAdView(Context context) {
        super(context);
        this.context = context;
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
        this.context = context;
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
        this.context = context;
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
        setTemplate(attrs);
        setAutoLoad(attrs);
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
        webView.clearCache(true);
        
        if (isAutoLoad) {
            webView.setWebViewClient(new OpenXAdWebViewClient());
        }
        
        addView(webView);
    }

    /**
     * Gets the html code from openx
     * 
     * @param zoneID
     * @param mode
     *            The mode how the ads will be retrieved
     * @return
     */
    private String getZoneTemplate(int zoneID, int mode) {
        Log.d(LOGTAG, "getZoneTemplateHtml() zoneID: " + zoneID);
        String openxHtml = "", zoneTag = "";

        zoneTag = String.format(URL_PLAIN, (hasHTTPS ? "https://" : "http://")
                + deliveryURL + '/' + jsTagURL, zoneID, getSource(),
                prng.nextLong(), softgamesAd.getViewportWidth(),
                softgamesAd.getPixelRatio(), softgamesAd.getGameName(),
                softgamesAd.getViewportHeight(),
                softgamesAd.getConnectionType(),
                softgamesAd.getDeviceManufacturer(), softgamesAd.getLocale(),
                softgamesAd.getCountry(), softgamesAd.getOs(),
                softgamesAd.getOsVersion(), softgamesAd.getIpAddress());

        if (mode == IN_IFRAME) {
            templateContext.setTemplateStratgy(new IframeTemplate());
            return templateContext.getTemplate(zoneTag);

        } else if (mode == DOWNLOAD_HTML) {
            try {
                DownloadHtmlTask htmlTask = new DownloadHtmlTask(context);
                openxHtml = htmlTask.execute(zoneTag).get();
                return openxHtml;

            } catch (InterruptedException e) {
                Log.e(LOGTAG, "", e);
            } catch (ExecutionException e) {
                Log.e(LOGTAG, "", e);
            }
        }
        return null;
    }

    /**
     * Gets the zone template.
     * 
     * @param zoneID
     *            the zone id
     * @return the zone template
     */
    @Deprecated
    protected String getZoneTemplate(int zoneID) {
        Log.d(LOGTAG, "getZoneTemplate() zoneID: " + zoneID);

        try {
            String zoneTag = String.format(URL_PLAIN, (hasHTTPS ? "https://"
                    : "http://") + deliveryURL + '/' + jsTagURL, zoneID,
                    source == null ? "" : URLEncoder.encode(source, "utf-8"),
                    prng.nextLong(), softgamesAd.getViewportWidth(),
                    softgamesAd.getPixelRatio(), softgamesAd.getGameName(),
                    softgamesAd.getViewportHeight(),
                    softgamesAd.getConnectionType());

            Log.e(LOGTAG, "URL_ENCODED: " + zoneTag);
            SGTemplate sgTemplate = SGTemplate.valueOf(template);

            switch (sgTemplate) {
            case LOADING_SCREEN:
                templateContext.setTemplateStratgy(new HtmlTemplate());
                return templateContext.getTemplate(zoneTag);
            default:
                templateContext.setTemplateStratgy(new IframeTemplate());
                return templateContext.getTemplate(zoneTag);

            }

        } catch (UnsupportedEncodingException e) {
            Log.wtf(LOGTAG, "UTF-8 not supported?!", e);
        }

        return null;
    }

    @Deprecated
    protected String getZoneTemplateHtml(int zoneID) {
        Log.d(LOGTAG, "getZoneTemplateHtml() zoneID: " + zoneID);

        String openxHtml = "";
        String zoneTag = "";

        try {
            zoneTag = String.format(URL_PLAIN, (hasHTTPS ? "https://"
                    : "http://") + deliveryURL + '/' + jsTagURL, zoneID,
                    source == null ? "" : URLEncoder.encode(source, "utf-8"),
                    prng.nextLong(), softgamesAd.getViewportWidth(),
                    softgamesAd.getPixelRatio(), softgamesAd.getGameName(),
                    softgamesAd.getViewportHeight(),
                    softgamesAd.getConnectionType());
            Log.e(LOGTAG, "URL_PLAIN: " + zoneTag);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(LOGTAG, "UTF-8 not supported?!", e);
        }
        try {
            DownloadHtmlTask htmlTask = new DownloadHtmlTask(context);
            openxHtml = htmlTask.execute(zoneTag).get();

        } catch (InterruptedException e) {
            Log.e(LOGTAG, "", e);
        } catch (ExecutionException e) {
            Log.e(LOGTAG, "", e);
        }

        return openxHtml;
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
        // If you do not want to load the ad manually comment this line out
        if (isAutoLoad) {
            load();
        }

    }

    /**
     * Load ad from OpenX server using the parameters that were set previously.
     * This will not work if the following minimum required parameters were not
     * set: delivery_url and zone_id.
     */
    public void load() {
        if (zoneID != null) {
            load(zoneID, DOWNLOAD_HTML);
        } else {
            Log.w(LOGTAG, "zoneID is empty");
        }
    }

    /**
     * Load the delivery URL in an IFRAME.
     */
    public void loadInIframe() {

        if (zoneID != null) {
            load(zoneID, IN_IFRAME);
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
    public void load(int zoneID, int mode) {
        Log.d(LOGTAG, "loadUrl with zoneID: " + zoneID);
        // check required parameters
        if (deliveryURL != null) {
            webView.loadDataWithBaseURL(null, getZoneTemplate(zoneID, mode),
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

    private void setTemplate(AttributeSet attrs) {
        int template_rs = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_TEMPLATE, -1);
        if (template_rs != -1) {
            this.template = res.getString(template_rs);
        } else {
            String html_template = attrs.getAttributeValue(ATTRS_NS,
                    PARAMETER_TEMPLATE);
            if (html_template != null) {
                this.template = html_template;
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

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public void setAutoLoad(AttributeSet attrs) {
        int autoload_rs = attrs.getAttributeResourceValue(ATTRS_NS,
                PARAMETER_AUTOLOAD, -1);
        if (autoload_rs != -1) {
            this.isAutoLoad = Boolean.valueOf(res.getString(autoload_rs));
        } else {
            boolean autoload = Boolean.valueOf(attrs.getAttributeValue(
                    ATTRS_NS, PARAMETER_AUTOLOAD));
            this.isAutoLoad = autoload;

        }
    }

}

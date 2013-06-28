package de.softgames.sdk.model;


/**
 * The Entity Class SoftgamesAd to encapsulate all the ads related attributes
 */
public class SoftgamesAd {

    private static final String OS = "Android";

    /** The game name. */
    private String gameName;

    /** The viewport width. */
    private Integer viewportWidth;

    /** The view port height. */
    private Integer viewPortHeight;

    /** The pixel ratio. */
    private Float pixelRatio;

    /** The connection type. */
    private int connectionType;

    private String deviceManufacturer;

    private String locale;

    private String country;

    private String os;

    private String osVersion;

    private String ipAddress;

    /**
     * Gets the game name.
     * 
     * @return the game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Instantiates a new softgames ad.
     * 
     * @param gameName
     *            the game name
     * @param viewportWidth
     *            the viewport width
     * @param viewPortHeight
     *            the view port height
     * @param pixelRatio
     *            the pixel ratio
     * @param connectionType
     *            the type of internet connectivity
     * @param deviceManufacturer
     *            the manufacturer name
     * @param locale
     * @param country
     * @param os
     *            Operating system name
     * @param osVersion
     *            Operating system version
     * 
     */
    public SoftgamesAd(String gameName, Integer viewportWidth,
            Integer viewPortHeight, Float pixelRatio, int connectionType,
            String deviceManufacturer, String locale, String country,
            String osVersion, String ipAddress) {
        super();
        this.gameName = gameName;
        this.viewportWidth = viewportWidth;
        this.viewPortHeight = viewPortHeight;
        this.pixelRatio = pixelRatio;
        this.connectionType = connectionType;
        this.deviceManufacturer = deviceManufacturer;
        this.locale = locale;
        this.country = country;
        this.os = OS;
        this.osVersion = osVersion;
        this.ipAddress = ipAddress;
    }

    public SoftgamesAd() {

    }

    /**
     * Sets the game name.
     * 
     * @param gameName
     *            the new game name
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Gets the viewport width.
     * 
     * @return the viewport width
     */
    public Integer getViewportWidth() {
        return viewportWidth;
    }

    /**
     * Sets the viewport width.
     * 
     * @param viewportWidth
     *            the new viewport width
     */
    public void setViewportWidth(Integer viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    /**
     * Gets the view port height.
     * 
     * @return the view port height
     */
    public Integer getViewportHeight() {
        return viewPortHeight;
    }

    /**
     * Sets the view port height.
     * 
     * @param viewPortHeight
     *            the new view port height
     */
    public void setViewPortHeight(Integer viewPortHeight) {
        this.viewPortHeight = viewPortHeight;
    }

    /**
     * Gets the pixel ratio.
     * 
     * @return the pixel ratio
     */
    public Float getPixelRatio() {
        return pixelRatio;
    }

    /**
     * Sets the pixel ratio.
     * 
     * @param pixelRatio
     *            the new pixel ratio
     */
    public void setPixelRatio(Float pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    /**
     * Gets the connection type.
     * 
     * @return the connection type
     */
    public int getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the connection type.
     * 
     * @param connectionType
     *            the new connection type
     */
    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "SoftgamesAd [gameName=" + gameName + ", viewportWidth="
                + viewportWidth + ", viewPortHeight=" + viewPortHeight
                + ", pixelRatio=" + pixelRatio + ", connectionType="
                + connectionType + ", deviceManufacturer=" + deviceManufacturer
                + ", locale=" + locale + ", country=" + country + ", os=" + os
                + ", osVersion=" + osVersion + ", ipAddress=" + ipAddress + "]";
    }

}

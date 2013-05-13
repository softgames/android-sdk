/**
 * 
 */
package de.softgames.sdk.util;


/**
 * The Enumeration NetworkType.
 * 
 * @author rolandcastillo
 */
public enum NetworkType {

    /** The unknown. */
    UNKNOWN(-1),
    /** The offline. */
    NONE(0),
    /** The gprs. */
    GPRS(1),
    /** The edge. */
    EDGE(2),
    /** The hsdpa. */
    HSDPA(3),
    /** The wifi. */
    WIFI(4);

    /** The value. */
    private final int value;
    
    /**
     * Instantiates a new network type.
     * 
     * @param type
     *            the type
     */
    NetworkType(int type) {
        this.value = type;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public int getValue() {
        return value;
    };

}

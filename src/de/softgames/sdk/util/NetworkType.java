/**
 * 
 */
package de.softgames.sdk.util;

/**
 * @author rolandcastillo
 *
 */
public enum NetworkType {
    UNKNOWN(-1), NONE(0), GPRS(1), EDGE(2), HSDPA(3), WIFI(4);

    private final int value;
    
    NetworkType(int type) {
        this.value = type;
    }

    public int getValue() {
        return value;
    };

}

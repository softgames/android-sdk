/**
 * 
 */
package de.softgames.sdk.util;

/**
 * @author rolandcastillo
 *
 */
public enum NetworkType {
    UNKNOWN(-1), NONE(0), WIFI(1), EDGE(2), GPRS(3), HSDPA(4);

    private final int value;
    
    NetworkType(int type) {
        this.value = type;
    }

    public int getValue() {
        return value;
    };

}

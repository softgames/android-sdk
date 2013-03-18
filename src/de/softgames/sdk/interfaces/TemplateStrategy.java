/**
 * 
 */
package de.softgames.sdk.interfaces;


/**
 * @author rolandcastillo
 * 
 *         Strategy Interface
 * 
 */
public interface TemplateStrategy {
    
    // We need to declare a variable with the 100% value since the parser does
    // not accept the percent sign
    /** The Constant IMG_WIDTH. */
    public static final String HUNDRED_PERCENT = "100%";

    public String getFormattedHTML(String zoneTag);


}



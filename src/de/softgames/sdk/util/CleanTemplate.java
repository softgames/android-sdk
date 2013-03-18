package de.softgames.sdk.util;


import de.softgames.sdk.interfaces.TemplateStrategy;


public class CleanTemplate implements TemplateStrategy {

    /** The Constant HTML_DOCUMENT_TEMPLATE. */
    private static final String HTML_DOCUMENT_TEMPLATE = "<html><head>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">"
            + "<style>*{padding: 0;margin: 0;}</style></head><body>"
            + "<iframe src=\"%1$s\" width=\"%2$s\" height=\"%2$s\" frameborder=\"0\"></iframe>"
            + "</pre></body></html>";

    @Override
    public String getFormattedHTML(String zoneTag) {
        String raw = String.format(HTML_DOCUMENT_TEMPLATE, zoneTag,
                HUNDRED_PERCENT);
        return raw;
    }

}
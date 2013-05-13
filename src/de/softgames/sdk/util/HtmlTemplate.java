package de.softgames.sdk.util;


import de.softgames.sdk.interfaces.TemplateStrategy;


public class HtmlTemplate implements TemplateStrategy {

    /** The Constant HTML_DOCUMENT_TEMPLATE. */
    private static final String HTML_DOCUMENT_TEMPLATE = "<html class=\"loadingScreen\"><head>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">"
            + "<style>* {padding: 0;margin: 0; background-color: transparent;}"
            + "body,html,#container{height:%2$s;}"
            + "#container{width:%2$s;position: relative;}img{width: %2$s;"
            + "position: absolute;top: 0;left: 0;right:0;bottom:0;margin: auto;}</style>"
            + "</head>\n<body><div style=\"display:table;height:%2$s;width:%2$s;\">%1$s</div>"
            + "</pre></body></html>";

    @Override
    public String getFormattedHTML(String zoneTag) {
        String raw = String.format(HTML_DOCUMENT_TEMPLATE, zoneTag, HUNDRED_PERCENT);
        return raw;
    }

}
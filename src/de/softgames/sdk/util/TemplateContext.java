package de.softgames.sdk.util;


import de.softgames.sdk.interfaces.TemplateStrategy;


public class TemplateContext {

    private TemplateStrategy strategy;

    public void setTemplateStratgy(TemplateStrategy strategy) {
        this.strategy = strategy;
    }

    public String getTemplate(String zoneTag) {
        return strategy.getFormattedHTML(zoneTag);
    }
}

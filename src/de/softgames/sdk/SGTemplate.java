package de.softgames.sdk;

public enum SGTemplate {

    CLEAN("CLEAN"), LOADING_SCREEN("LOADING_SCREEN");

    private final String sValue;

    SGTemplate(String value) {
        this.sValue = value;
    }

    public String getValue() {
        return sValue;
    }

}

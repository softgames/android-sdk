package de.softgames.sdk.exceptions;


public class IllegalLauncherActivityException extends RuntimeException {

    /**
     * Default serial ID
     */
    private static final long serialVersionUID = 1L;

    public IllegalLauncherActivityException() {
        super();

    }

    public IllegalLauncherActivityException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

    public IllegalLauncherActivityException(String detailMessage) {
        super(detailMessage);
    }

    public IllegalLauncherActivityException(Throwable throwable) {
        super(throwable);
    }

}

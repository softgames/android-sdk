package de.softgames.sdk;


import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;


public class OpenXAdWebChromeClient extends WebChromeClient {
    private static final String LOGTAG = OpenXAdWebChromeClient.class
            .getSimpleName();

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d(LOGTAG,
                consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
        return true;
    }
}

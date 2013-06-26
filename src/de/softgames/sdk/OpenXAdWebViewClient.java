package de.softgames.sdk;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.softgames.sdk.ui.SoftgamesUI;

public class OpenXAdWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        
        // Open the more games dialog if the more games button is clicked 
        SoftgamesUI.buildMoreGamesDialog(view.getContext()).show();
        
     // The google analytics object instance
        GoogleAnalytics mInstance = GoogleAnalytics.getInstance(view.getContext());
        // Get the existing tracker
        Tracker mTracker = mInstance.getDefaultTracker();
        mTracker.sendView("/MoreGamesScreen");
        
        return true;
    }


}

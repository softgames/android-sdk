package de.softgames.sdk;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.softgames.sdk.ui.SoftgamesUI;

public class OpenXAdWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        
        // Open the more games dialog if the more games button is clicked 
        SoftgamesUI.buildMoreGamesDialog(view.getContext()).show();
                
        return true;
    }


}

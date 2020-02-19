package com.mindlinksw.schoolmeals.view.webview;

import android.app.Activity;

public class WebBaseScriptInterface {

    // Log Tag
    private static final String TAG = WebBaseScriptInterface.class.getName();

    // Variable
    protected Activity mActivity;
    protected CustomWebView mWebView;

    // Constructor
    public WebBaseScriptInterface(Activity activity, CustomWebView webView) {
        this.mActivity = activity;
        this.mWebView = webView;
    }

}
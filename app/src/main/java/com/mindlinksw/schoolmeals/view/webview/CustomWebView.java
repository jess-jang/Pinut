package com.mindlinksw.schoolmeals.view.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mindlinksw.schoolmeals.interfaces.WebViewListener;
import com.mindlinksw.schoolmeals.utils.Logger;

import java.io.Serializable;


public class CustomWebView extends WebView implements Serializable {

    private static final String TAG = CustomWebView.class.getName();

    private Activity mActivity;
//    private ProgressView mProgressView;

    private WebBaseChromeClient mWebChromeClient;
    private WebBaseViewClient mWebViewClient;
    private WebBaseScriptInterface mWebScriptInterface;

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomWebView initView(Activity activity) {
        this.mActivity = activity;

        setUserAgent();
        setWebBaseViewClient();
        setWebBaseChromeClient();
        setWebBaseScriptInterface();

        return this;
    }

//    /**
//     * Progress View
//     */
//    public CustomWebView setProgress(ProgressView progressView) {
//        this.mProgressView = progressView;
//
//        if (mWebViewClient != null) {
//            mWebViewClient.setProgressView(progressView);
//        }
//
//        if (mWebChromeClient != null) {
//            mWebChromeClient.setProgressView(progressView);
//        }
//
//        return this;
//    }

    /**
     * UserAgent
     */
    public void setUserAgent() {

        getSettings().setJavaScriptEnabled(true);

        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setSupportMultipleWindows(true);

        getSettings().setDatabaseEnabled(true);
        getSettings().setDomStorageEnabled(true);

        // Setting - Settings for KITKAT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        // Setting - Settings for LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Setting - Mixed Content
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            // Setting - Cookie Policy
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }

        // Cache mode
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        // Android 7.0 이상 버그 - Web Input Text Autocomplete 활성화시 스크롤 최상단으로 이동 버그
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Default true
            getSettings().setSaveFormData(false);
        }
    }

    /**
     * WebViewClient
     */
    public void setWebBaseViewClient() {

        mWebViewClient = new WebBaseViewClient(mActivity);
        setWebViewClient(mWebViewClient);

    }

    /**
     * WebChromeClient
     */
    public void setWebBaseChromeClient() {

        mWebChromeClient = new WebBaseChromeClient(mActivity);
        setWebChromeClient(mWebChromeClient);

    }

    /**
     * AddJavascriptInterface
     */
    @SuppressLint("JavascriptInterface")
    public void setWebBaseScriptInterface() {

        mWebScriptInterface = new WebBaseScriptInterface(mActivity, this);
        addJavascriptInterface(mWebScriptInterface, "device");

    }

    // 콜 자바스크립트
    public void callJavaScript(final String call) {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.e(TAG, "javascript:" + call);
                        loadUrl("javascript:" + call);
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener
     */
    // 타이틀 리스너
    public void setOnReceivedTitleListener(WebViewListener.OnReceivedTitleListener listener) {
        if (mWebChromeClient != null) {
            mWebChromeClient.setOnReceivedTitleListener(listener);
        }
    }

    // 페이지 start, finish
    public void setOnProgressChangedListener(WebViewListener.OnProgressChangedListener listener) {
        if (mWebChromeClient != null) {
            mWebChromeClient.setOnProgressChangedListener(listener);
        }
    }

    // 페이지 progress
    public void setOnPageStatusListener(WebViewListener.OnPageStatusListener listener) {
        if (mWebChromeClient != null) {
            mWebViewClient.setOnPageStatusListener(listener);
        }
    }
}


package com.mindlinksw.schoolmeals.view.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mindlinksw.schoolmeals.interfaces.WebViewListener;

/**
 * @author 장세진
 * @description WebChromeClient for WebView
 */
public class WebBaseChromeClient extends WebChromeClient {

    // Log Tag
    private static final String TAG = WebBaseChromeClient.class.getName();

    // Variable
    private Activity mActivity;
//    private ProgressView mProgressView;
//    private ProgressBar mProgressBar;

    private WebViewListener.OnReceivedTitleListener mTitleListener;
    private WebViewListener.OnProgressChangedListener mOnProgressChangedListener;

    public WebBaseChromeClient(Activity activity) {
        this.mActivity = activity;
    }

    // progress
//    public void setProgressView(ProgressView progressView) {
//        this.mProgressView = progressView;
//    }

    // progress
//    public void setProgressBar(ProgressBar progressBar) {
//        this.mProgressBar = progressBar;
//    }

    public void setOnReceivedTitleListener(WebViewListener.OnReceivedTitleListener listener) {
        this.mTitleListener = listener;
    }

    public void setOnProgressChangedListener(WebViewListener.OnProgressChangedListener listener) {
        this.mOnProgressChangedListener = listener;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        if (mActivity != null && !mActivity.isFinishing()) {
//                new AlertDialog(mActivity)
//                        .setTitle(message)
//                        .setPositiveButton(mActivity.getString(android.R.string.ok), new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick() {
//                                result.confirm();
//                            }
//                        })
//                        .setAlertCancelable(false)
//                        .alert();
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        if (mActivity != null && !mActivity.isFinishing()) {
//                new AlertDialog(mActivity)
//                        .setMessage(message)
//                        .setPositiveButton(mActivity.getString(android.R.string.ok), new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick() {
//                                result.confirm();
//                            }
//                        })
//                        .setNegativeButton(mActivity.getString(android.R.string.cancel), new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick() {
//                                result.cancel();
//                            }
//                        })
//                        .setAlertCancelable(false)
//                        .alert();
        }
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, final JsResult result) {
        if (mActivity != null && !mActivity.isFinishing()) {
//            new AlertDialog(mActivity)
//                    .setTitle(message)
//                    .setPositiveButton(mActivity.getString(android.R.string.ok), new AlertDialog.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            result.confirm();
//                        }
//                    })
//                    .setNegativeButton(mActivity.getString(android.R.string.cancel), new AlertDialog.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            result.cancel();
//                        }
//                    })
//                    .setAlertCancelable(false)
//                    .alert();
        }
        return true;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if (mOnProgressChangedListener != null) {
            mOnProgressChangedListener.onProgressChanged(newProgress);
        }

//        if (mProgressBar != null) {
//            if (newProgress >= 100) {
//                mProgressBar.setVisibility(View.GONE);
//            }
//
//            mProgressBar.setProgress(newProgress);
//        }
//
//        if (mProgressView != null) {
//            if (newProgress >= 100) {
//                mProgressView.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (mTitleListener != null) {
            mTitleListener.onReceivedTitle(title);
        }
    }
}
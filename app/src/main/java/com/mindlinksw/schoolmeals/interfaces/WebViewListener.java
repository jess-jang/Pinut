package com.mindlinksw.schoolmeals.interfaces;

/**
 * Created by N16326 on 2018. 8. 2..
 */

public interface WebViewListener {

    // 웹 타이틀 리스너
    public interface OnReceivedTitleListener {
        public void onReceivedTitle(String title);
    }

    // page progress
    public interface OnProgressChangedListener {
        public void onProgressChanged(int progress);
    }

    // page status
    public interface OnPageStatusListener {
        public void onPageStarted(String url);
        public void onPageFinished(String url);
    }


}

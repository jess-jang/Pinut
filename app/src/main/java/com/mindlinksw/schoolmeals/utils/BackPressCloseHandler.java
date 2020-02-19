package com.mindlinksw.schoolmeals.utils;

import android.app.Activity;

import com.google.android.material.snackbar.Snackbar;
import com.mindlinksw.schoolmeals.R;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;

    private Activity mActivity;
    private Snackbar mSnackbar;

    public BackPressCloseHandler(Activity activity) {
        this.mActivity = activity;
    }

    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            if (mSnackbar != null) {
                mSnackbar.dismiss();
            }

            mActivity.finish();
        }
    }

    public void showGuide() {
        mSnackbar = Snackbar.make(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.snack_exit), Snackbar.LENGTH_SHORT);
        mSnackbar.show();
    }

}
package com.mindlinksw.schoolmeals.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.view.activity.LoginActivity

/**
 * Created by N16326 on 2019. 3. 11..
 */

object SnackBarUtils {

    /**
     * 로그인화면 이동
     */
    @JvmStatic
    fun login(activity: Activity) {

        try {

            val view = activity.findViewById<View>(android.R.id.content)
            Snackbar.make(view, R.string.snack_login_move, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.snack_login_action) {
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                    }
                    .show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

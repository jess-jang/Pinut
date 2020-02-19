package com.mindlinksw.schoolmeals.utils

import android.app.Activity
import com.mindlinksw.schoolmeals.R

/**
 * 액티비티 화면전환
 *
 */
class ActivityTransitions {

    companion object {

        enum class Type constructor(
            val enter: Int,
            val exit: Int
        ) {
            FADE_IN_OUT(R.anim.fade_in, R.anim.fade_out),
        }

        fun run(
            activity: Activity,
            type: Type?
        ) {
            if (type == null) {
                return
            }
            activity.overridePendingTransition(type.enter, type.exit)
        }

    }

}
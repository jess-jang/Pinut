package com.mindlinksw.schoolmeals.consts

import com.mindlinksw.schoolmeals.utils.Utils

object APIConst {

    fun apkKey(): String {
        return if (Utils.isTest()) {
            "6e730213-8286-4ccb-92a7-4169856c8cd9"
        } else {
            "6e730213-8286-4ccb-92a7-4169856c8cd9"
        }
    }


}

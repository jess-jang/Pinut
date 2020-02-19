package com.mindlinksw.schoolmeals.consts

import com.mindlinksw.schoolmeals.utils.Utils

object HostConst {

    @JvmStatic
    fun apiHost(): String {

        return if (Utils.isTest()) {
            "http://www.schoolmeals.site/school_meal_api/"
        } else {
            "http://www.schoolmeals.site/school_meal_api/"
        }

    }

}

package com.mindlinksw.schoolmeals.consts

/**
 * Created by N16326 on 2018. 11. 26..
 */

class Style {

    object MAIN {
        val BANNER: Int = 1 // 배너
        val MEALS: Int = 2 // 식단표
        val OUR_SCHOOL: Int = 3 // 우리 학교만 보기
        val BORAD: Int = 1001 // 게시물
        val AD: Int = 2001 // 광고
        val LAST: Int = 98 // 마지막
        val EMPTY: Int = 99 // 공백
    }

    object VIDEO {
        val BORAD: Int = 4001 // 게시물
        val AD: Int = 2001 // 광고
    }

    object WRITE {
        const val WRITE: Int = 1
        const val MODIFY: Int = 2
    }

    object WEEKLY {
        const val TITLE: Int = 1
        const val MEAL: Int = 2
    }

    object TERM_TYPE {
        const val TERM = 1
        const val PRIVATE = 2
    }

}

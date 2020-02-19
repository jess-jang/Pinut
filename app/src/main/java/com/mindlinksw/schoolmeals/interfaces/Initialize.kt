package com.mindlinksw.schoolmeals.interfaces

import android.view.View
import org.jetbrains.annotations.Nullable

/**
 * Activity, Fragment 기본 초기화
 */
interface Initialize {

    fun initDataBinding() // 데이터 바인딩
    fun initLayout(@Nullable view: View?) // 레이아웃
    fun initVariable() // 변수
    fun initEventListener() // 이벤트 리스너


}
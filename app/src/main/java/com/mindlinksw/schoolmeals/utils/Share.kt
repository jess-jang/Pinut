package com.mindlinksw.schoolmeals.utils

import android.app.Activity
import android.util.Log
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback

/**
 * Created by N16326 on 2018. 11. 5..
 */

object Share {

    private val TAG = Share::class.java.name

    /**
     * 카카오 공유하기
     */
    @JvmStatic
    fun kakao(activity: Activity) {

        try {

            val params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder(
                            "message",
                            "img",
                            LinkObject.newBuilder()
//                                    .setAndroidExecutionParams("url=" + URLEncoder.encode(url, HTTP.UTF_8))
//                                    .setIosExecutionParams("url=" + URLEncoder.encode(url, HTTP.UTF_8))
                                    .build())
                            .setDescrption("Descrption")
                            .build())
                    .addButton(ButtonObject("sns_kakao_show_web", LinkObject.newBuilder()
                            .setMobileWebUrl("url")
                            .build()))
                    // kakaolink://url= 로 전달됨
                    .addButton(ButtonObject("sns_kakao_show_app", LinkObject.newBuilder()
//                            .setAndroidExecutionParams("url=" + URLEncoder.encode(url, HTTP.UTF_8))
//                            .setIosExecutionParams("url=" + URLEncoder.encode(url, HTTP.UTF_8))
                            .build()))
                    .build()

            KakaoLinkService.getInstance().sendDefault(activity, params, object : ResponseCallback<KakaoLinkResponse>() {

                override fun onSuccess(result: KakaoLinkResponse) {
                    Log.d("Kakao", "Kakao : " + result.warningMsg.toString())
                }

                override fun onFailure(errorResult: ErrorResult) {
                    Log.d("Kakao", "Kakao : " + errorResult.errorMessage)
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

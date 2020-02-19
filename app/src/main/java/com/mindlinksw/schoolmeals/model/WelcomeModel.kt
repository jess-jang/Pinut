package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "list":[
//    {
//        "adId":15,
//        "tit":"로그인 알 지급",
//        "content":"기본 광고",
//        "linkYn":"N",
//        "adLink":"NULL",
//        "startDate":"2019-08-28",
//        "stopDate":"2220-01-01",
//        "mediaId":414,
//        "thumbnailFileUrl":"https://mindlink.s3.ap-northeast-2.amazonaws.com/schoolmeal_ad/welcome_test.jpg"
//    }
//    ]
//}


data class WelcomeModel(val list: ArrayList<WelcomeItem>
) : Serializable

data class WelcomeItem(val adId: Int,
                       val tit: String,
                       val content: String,
                       val linkYn: String,
                       val adLink: String,
                       val startDate: String,
                       val stopDate: String,
                       val mediaId: Int,
                       val thumbnailFileUrl: String
) : Serializable
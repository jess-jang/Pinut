package com.mindlinksw.schoolmeals.model

import java.io.Serializable


//{
//    "list":[
//    {
//        "adId":광고 아이디,
//        "tit":광고 제목,
//        "content":광고 내용,
//        "adLink":광고 링크,
//        "linkYn":링크유무,
//        "startDate":시작일,
//        "stopDate":종료일,
//        "mediaId":미디어파일 아이디,
//        "thumbnailFileUrl":이미지 파일 URL,
//​        "backColor":배경색상
//    }
//    ]
//}

data class BannerListModel(var list: ArrayList<BannerItem>) : Serializable

data class BannerItem(
        var adId: Int,
        var tit: String,
        var content: String,
        var adLink: String,
        var linkYn: String,
        var startDate: String,
        var stopDate: String,
        var mediaId: Int,
        var thumbnailFileUrl: String,
        var backColor: String
) : Serializable
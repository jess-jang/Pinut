package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "adMap":{
//    "adId":광고 번호,
//    "type":타입,
//    "section":구분,
//    "tit":타이틀,
//    "content":내용,
//    "linkYn":링크 유무,
//    "adLink":링크 URL,
//    "useAt":사용 유무,
//    "mediaId":미디어 아이디,
//    "persent":노출 빈도수,
//    "listMediaId":미디어 아이디2,
//    "bannerFileUrl":배너 이미지 URL,
//    "listFileUrl":리스트 이미지 URL
//}
//}

data class NoAdModel(var adMap: NoAdItem) : Serializable

data class NoAdItem(
        var adId: Int,
        var type: String,
        var section: String,
        var tit: String,
        var content: String,
        var linkYn: String,
        var adLink: String,
        var useAt: String,
        var mediaId: Int,
        var persent: Int,
        var listMediaId: Int,
        var bannerFileUrl: String,
        var listFileUrl: String
) : Serializable
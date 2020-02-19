package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "curPage":전체 페이지,
//    "totalPage":전체 페이지 개수,
//    "fromPage":화면에 표시될 페이지 시작지점,
//    "pageSize":화면에 표시될 페이지 개수,
//    "list":[
//    {
//        "rownum":순번,
//        "articleId":게시물 아이디,
//        "tit":제목,
//        "content":내용,
//        "mediaId":첨부파일 아이디,
//        "adLink":광고 URL,
//        "nckNm":닉네임,
//        "prflPhotoUrl":프로필 사진 URL,
//        "webLinkUrl": 웹 링크 연결 URL,
//        "thumbnailUrl1":섬네일 이미지 URL,
//        "thumbnailUrl2":섬네일 이미지 URL,
//        "thumbnailUrl3":섬네일 이미지 URL,
//        "thumbnailCnt":섬네일 이미지 개수,
//        "regDate":작성시간,
//        "day":지난 시간 계산용,
//        "hour":지난 시간 계산용,
//        "minute":지난 시간 계산용,
//        "timeAgo":지난 시간
//    }
//    ]
//}
data class NoticeModel(var curPage: Int,
                       var totalPage: Int,
                       var fromPage: Int,
                       var pageSize: Int,
                       var list: ArrayList<NoticeItem>) : Serializable

data class NoticeItem(
        var webLinkUrl: String,
        var nckNm: String,
        var regDate: String,
        var prflPhotoUrl: String,
        var rownum: String = "",
        var articleId: Int = 0,
        var tit: String = "",
        var content: String = "",
        var mediaId: String = "",
        var adLink: String = "",
        var thumbnailUrl1: String = "",
        var thumbnailUrl2: String = "",
        var thumbnailUrl3: String = "",
        var thumbnailCnt: Int = 0,
        var day: Int = 0,
        var hour: Int = 0,
        var minute: Int = 0,
        var timeAgo: String = ""
) : Serializable








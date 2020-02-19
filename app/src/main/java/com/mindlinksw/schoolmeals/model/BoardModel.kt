package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "curPage":현재페이지,
//    "totalPage":전체 페이지 개수,
//    "fromPage":화면에 표시될 페이지 시작지점,
//    "pageSize":화면에 표시될 페이지 개수,
//    "list":[
//    {
//        "rownum":순번,
//        "boardId":게시물 아이디,
//        "ctgryCode":카테고리 코드,
//        "type":게시물 타입,
//        "tit":제목,
//        "esntlId":유저 구분 아이디,
//        "userId":작성자 소셜 아이디,
//        "userSocial":작성자 소셜 구분,
//        "nckNm":닉네임,
//        "thumbnailUrl1":섬네일 이미지 URL,
//        "thumbnailUrl2":섬네일 이미지 URL​,
//        "thumbnailUrl3":섬네일 이미지 URL,
//        "thumbnailCnt":게시물 이미지 개수,
//        "recommendCnt":추천수,
//        "commentCnt":댓글 수,
//        "bkmrkSttus":북마크 여부,
//        "mediaId":미디어 파일 아이디,
//        "adLink":광고 클릭 링크,
//        "viewCnt":조회 수,
//        "regDate":작성날짜,
//        "day":지난시간 계신용,
//        "hour":지난시간 계신용​,
//        "minute":지난시간 계신용​,
//        "content":글 내용,
//        "timeAgo":작성날짜 지난시간
//    }
//    ]
//}​

data class BoardModel(var curPage: Int,
                      var totalPage: Int,
                      var fromPage: Int,
                      var pageSize: Int,
                      var list: ArrayList<BoardItem>) : Serializable

data class BoardItem(var type: Int,
                     var rownum: String,
                     var boardId: Int,
                     var ctgryCode: String,
                     var tit: String,
                     var esntlId: String,
                     var userId: String,
                     var userSocial: String,
                     var nckNm: String,
                     var thumbnailUrl1: String,
                     var thumbnailUrl2: String,
                     var thumbnailUrl3: String,
                     var thumbnailCnt: Int,
                     var recommendCnt: Int,
                     var commentCnt: Int,
                     var bkmrkSttus: String,
                     var mediaId: Int,
                     var adLink: String,
                     var viewCnt: Int,
                     var regDate: String,
                     var day: Int,
                     var hour: Int,
                     var minute: Int,
                     var content: String,
                     var timeAgo: String,
                     var recommendYn: String,
                     var position: Int,
                     var duration: String,
                     var youtubeMediaKey: String,
                     var linkYn: String
) : Serializable {

    constructor() : this(0, "", 0, "", "", "", "", "", "",
            "", "", "", 0, 0, 0, "",
            0, "", 0, "", 0, 0, 0, "", "", "", -1, "", "", "")

    // type
    constructor(type: Int) : this(type, "", 0, "", "", "", "", "", "",
            "", "", "", 0, 0, 0, "",
            0, "", 0, "", 0, 0, 0, "", "", "", -1, "", "", "")
}

// 글 등록 결과
data class BoardInsertModel(var code: String,
                            var boardId: Int,
                            var error: String) : Serializable

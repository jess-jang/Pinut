package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    var curPagevar :String,현재 페이지,
//    var totalPagevar :String,전체 페이지 개수,
//    var fromPagevar :String,화면에 표시될 페이지 시작지점,
//    var pageSizevar :String,화면에 표시될 페이지 개수,
//    var listvar :String,[
//    {
//        var rownumvar :String,순번,
//        var boardIdvar :String,게시물 아이디,
//        var ctgryCodevar :String,카테고리 코드,
//        var typevar :String,게시물 타입,
//        var titvar :String,제목,
//        var esntlIdvar :String,유저 구분 아이디,
//        var userIdvar :String,유저 소셜 아이디,
//        var userSocialvar :String,유저 소셜 구분자,
//        var nckNmvar :String,유저 닉네임,
//        var thumbnailUrl1var :String,섬네일 이미지 URL,
//        var thumbnailUrl2var :String,섬네일 이미지 URL,
//        var thumbnailUrl3var :String,섬네일 이미지 URL,
//        var thumbnailCntvar :String,섬네일 이미지 개수,
//        var recommendCntvar :String,좋아요 개수,
//        var commentCntvar :String,댓글 개수,
//        var bkmrkSttusvar :String,본인 북마크 여부,
//        var mediaIdvar :String,미디어 파일 아이디,
//        var adLinkvar :String,광고 링크,
//        var viewCntvar :String,조회수,
//        var regDatevar :String,작성날짜,
//        var dayvar :String,지난시간 계산용,
//        var hourvar :String,지난시간 계산용,
//        var minutevar :String,지난시간 계산용,
//        var contentvar :String,글 내용,
//        var timeAgovar :String,지난 시간
//    }
//    ]
//}
data class BookmarkModel(var curPage: Int,
                         var totalPage: Int,
                         var pageSize: Int,
                         var list: ArrayList<BookmarkItem>) : Serializable

data class BookmarkItem(
        var rownum: String,
        var boardId: Int,
        var ctgryCode: String,
        var type: Int,
        var tit: String,
        var esntlId: String,
        var userId: String,
        var userSocial: String,
        var nckNm: String,
        var thumbnailUrl1: String,
        var thumbnailUrl2: String,
        var thumbnailUrl3: String,
        var thumbnailCnt: String,
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
        var bookMarkIcon: String
) : Serializable






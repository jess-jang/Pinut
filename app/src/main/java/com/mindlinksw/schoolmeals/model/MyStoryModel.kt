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
//        "prvtType":글 종류,
//        "ctgryCode":카테고리 키,
//        "ctgryNm":카테고리 이름,
//        "boardId":게시물 아이디,
//        "tit":게시물 이름,
//        "parentCommentId":상위 댓글 아이디,
//        "parentCommentEsntlId":상위 댓글 작성자 구분 아이디,
//        "parentCommentUserId":상위 댓글 작성자 소셜 아이디,
//        "parentCommentUserSocial":상위 댓글 작성자 소셜 구분자,
//        "parentCommentNckNm":상위 댓글 작성자 닉네임,
//        "commentId":댓글 아이디,
//        "commentContent":댓글 내용,
//        "userId":게시글 / 댓글 작성자,
//        "regDate":작성날짜,
//        "day":지난시간 계산용,
//        "hour":지난시간 계신용,
//        "minute":지난시간 계산용,
//        "timeAgo":지난시간
//    }
//    ]
//}
data class MyStoryModel(var curPage: Int,
                        var totalPage: Int,
                        var pageSize: Int,
                        var fromPage: Int,
                        var list: ArrayList<MyStoryItem>) : Serializable

data class MyStoryItem(var rownum: String,
                       var prvtType: String,
                       var ctgryCode: String,
                       var ctgryNm: String,
                       var boardId: Int,
                       var tit: String,
                       var parentCommentId: String,
                       var parentCommentEsntlId: String,
                       var parentCommentUserId: String,
                       var parentCommentUserSocial: String,
                       var parentCommentNckNm: String,
                       var commentId: Int,
                       var commentContent: String,
                       var userId: String,
                       var regDate: String,
                       var day: Int,
                       var hour: Int,
                       var minute: Int,
                       var timeAgo: String,
                       var prvtText: String,
                       var prvtIcon: String,
                       var target: String
) : Serializable






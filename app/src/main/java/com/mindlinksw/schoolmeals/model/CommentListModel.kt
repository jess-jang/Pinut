package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    var  curPagevar  :현재페이지,
//    var  totalPagevar  :전체 페이지 개수,
//    var  fromPagevar  :화면에 표시될 페이지 시작지점,
//    var  pageSizevar  :화면에 표시될 페이지 개수,
//    var  listvar  :[
//    {
//        var  commentIdvar  :댓글 아이디,
//        var  depthvar  :깊이,
//        var  replyCntvar  :달린 대댓글 갯수,
//        var  esntlIdvar  :작성자 구분 아이디,
//        var  userIdvar  :작성자 소셜 아이디,
//        var  userSocialvar  :작성자 소셜 구분자,
//        var  nckNmvar  :작성자 닉네임,
//        var  replyEsntlIdvar  :대댓글 지칭 구분 아이디,
//        var  replyUserIdvar  :대댓글 지칭 소셜 아이디,
//        var  replyUserSocialvar  :대댓글 지칭 소셜 구분자,
//        var  replyNckNmvar  :대댓글 지칭 닉네임,
//        var  contentvar  :댓글 내용,
//        var  recommendCntvar  :추천수,
//        var  regDatevar  :작성날짜,
//        var  dayvar  :지난 시간 계산용,
//        var  hourvar  :지난 시간 계산용,
//        var  minutevar  :지난 시간 계산용,
//        var  timeAgovar  :지난 시간
//    }
//    ]
//}
data class CommentListModel(var curPage: Int,
                            var totalPage: Int,
                            var fromPage: Int,
                            var pageSize: Int,
                            var list: ArrayList<CommentItem>) : Serializable

data class CommentItem(var commentId: Int,
                       var parentId: Int,
                       var depth: Int,
                       var replyCnt: Int,
                       var esntlId: String,
                       var userId: String,
                       var userSocial: String,
                       var nckNm: String,
                       var replyEsntlId: String,
                       var replyUserId: String,
                       var replyUserSocial: String,
                       var replyNckNm: String,
                       var content: String,
                       var recommendCnt: Int,
                       var regDate: String,
                       var day: Int,
                       var hour: Int,
                       var minute: Int,
                       var timeAgo: String,
                       var recommendYn: String,
                       var levelIcon: String,
                       var userLevel: Int,
                       var delYn: String,
                       var writerYn: String,
                       var list: ArrayList<CommentItem>?

) : Serializable {
    constructor() :
            this(0, 0, 0, 0, "", "", "", "",
                    "", "", "", "", "", 0,
                    "", 0, 0, 0, "", "", "", 0, "", "", null)

    constructor(commentId: Int, parentId: Int, depth: Int, replyCnt: Int, nckNm: String, content: String, recommendCnt: Int, timeAgo: String) :
            this(commentId, parentId, depth, replyCnt, "", "", "", nckNm,
                    "", "", "", "", content, recommendCnt,
                    "", 0, 0, 0, timeAgo, "", "", 0, "", "", null)
}







package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    var curPagevar :현재페이지,
//    var totalPagevar :전체 페이지 개수,
//    var fromPagevar :화면에 표시될 페이지 시작지점,
//    var pageSizevar :화면에 표시될 페이지 개수,
//    var listvar :[
//    {
//        var rownumvar :순번,
//        var alertIdvar :알림 아이디,
//        var alertTypevar :알림 종류,
//        var ctgryCodevar :카테고리 코드,
//        var ctgryNmvar :카테고리 이름,
//        var boardIdvar :게시물 아이디,
//        var adLinkvar :광고 링크,
//        var userIdvar :사용자 아이디,
//        var alertTextvar :알림 텍스트,
//        var alertYnvar :사용자 확인 여부,
//        var regDatevar :알림 시간,
//        var dayvar :지난 시간 계산용,
//        var hourvar :지난 시간 게산용,
//        var minutevar :지난 시간 계산용,
//        var timeAgovar :지난 시간
//    }
//    ]
//}
data class NotificationModel(var curPage: Int,
                             var totalPage: Int,
                             var pageSize: Int,
                             var list: ArrayList<NotificationItem>) : Serializable

data class NotificationItem(var rownum: String,
                            var alertId: Int,
                            var alertType: String,
                            var alertIcon: String,
                            var ctgryCode: String,
                            var ctgryNm: String,
                            var boardId: Int,
                            var adLink: String,
                            var content: String,
                            var userId: String,
                            var alertText: String,
                            var alertYn: String,
                            var regDate: String,
                            var trggrNckNm: String,
                            var day: Int,
                            var hour: Int,
                            var minute: Int,
                            var timeAgo: String,
                            var target: String
) : Serializable






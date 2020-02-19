package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "curPage":현재 페이지,
//    "totalPage":전체 페이지 개수,
//    "fromPage":화면에 표시될 페이지 시작지점,
//    "pageSize":화면에 표시될 페이지 개수,
//    "list":[
//    {
//        "rownum":순번,
//        "userId":사용자 구분 아이디,
//        "sn":순서,
//        "point":포인트,
//        "useType":포인트 사용/적립 사유 타입,
//        "pointType":포인트 사용/적립 타입,
//        "sign":포인트 사용/적립 부호,
//        "cause":사유,
//        "regDate":생성 날짜,
//        "day":지난시간 계산용,
//        "hour":지난시간 계산용,
//        "minute":지난시간 계산용,
//        "timeAgo":지난 시간
//    }
//    ]
//}
data class PointModel(var curPage: Int,
                      var totalPage: Int,
                      var pageSize: Int,
                      var userPoint: Int,
                      var list: ArrayList<PointItem>) : Serializable

data class PointItem(var rownum: String,
                     var userId: String,
                     var sn: Int,
                     var point: Int,
                     var useType: String,
                     var pointType: String,
                     var sign: String,
                     var cause: String,
                     var regDate: String,
                     var day: Int,
                     var hour: Int,
                     var minute: Int,
                     var timeAgo: String,
                     var icon: String
) : Serializable






package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "result":{
//    "prevSearchDate":이전 검색 기준 날짜,
//    "searchDate":검색 기준 날짜,
//    "weekNum":주차,
//    "nextSearchDate":다음 검색 기준 날짜,
//    "dateList":[
//    {
//        "date":날짜,
//        "dweek":요일,
//        "lunch":{
//        "date":날짜,
//        "mealId":급식 키,
//        "kcal":칼로리,
//        "dataYn":검색기준키,
//        "nullYn":검색기준키,
//        "updYn":검색기준키,
//        "mealType":아침,점심,저녁 구분,
//        "dweek":요일,
//        "codeNm":학교코드,
//        "insttNm":학교이름,
//        "menuList":[
//        {
//            "rownum":순번,
//            "menuNm":메뉴명,
//            "alrgyYn":알레르기 유무
//        }
//        ]
//    }
//    }
//    ]
//}
//}

// 주간식간 데이터 모델
data class WeeklyMealModel(var result: WeeklyMealItem) : Serializable

data class WeeklyMealItem(
        var prevSearchDate: String,
        var searchDate: String,
        var weekNum: Int,
        var nextSearchDate: String,
        var dateList: ArrayList<WeeklyMealDateItem>
) : Serializable

data class WeeklyMealDateItem(
        var date: String,
        var dweek: String,
        var lunch: WeeklyMealTypeItem,
        var breakfast: WeeklyMealTypeItem,
        var dinner: WeeklyMealTypeItem
) : Serializable

data class WeeklyMealTypeItem(
        var date: String,
        var mealId: Int,
        var kcal: String,
        var dataYn: String,
        var nullYn: String,
        var updYn: String,
        var mealType: String,
        var dweek: String,
        var codeNm: String,
        var insttNm: String,
        var menuList: ArrayList<WeeklyMealMenuItem>
) : Serializable

data class WeeklyMealMenuItem(
        var rownum: Int,
        var menuNm: String,
        var comma: String,
        var alrgyYn: String
) : Serializable

// 주간식단 화면에 보여주기 위한 모델
data class WeeklyDisplayModel(
        var style: Int,
        var content: String,
        var mealType: String
) : Serializable








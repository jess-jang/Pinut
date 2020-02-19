package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "list":[
//    {
//        "date":날짜,
//        "codeNm":학교코드,
//        "mealType":식사구분,
//        "nullYn":데이터 유무,
//        "updDate":업데이트 날짜,
//        "kcal":칼로리,
//        "updYn":업데이트 필요 유무,
//        "dweek":요일,
//        "endDate":아침,점심,저녁 구분에 따른 종료 시간,
//        "menuList":[
//        {
//            "rownum":순번,
//            "menuNm":메뉴명,
//            "alrgyYn":알레르기 유무
//        }
//        ],
//        ​"menuCnt":메뉴 갯수
//    }
//    ]
//}

data class MainMealModel(var list: ArrayList<MainMealItem>) : Serializable

data class MainMealItem(
        var date: String,
        var codeNm: String,
        var mealType: String,
        var nullYn: String,
        var updDate: String,
        var kcal: String,
        var updYn: String,
        var dweek: String,
        var endDate: String,
        var isLast: Boolean,
        var menuCnt: Int,
        var menuList: ArrayList<WeeklyMealMenuItem>
) : Serializable {
    constructor(isLast: Boolean) :
            this("", "", "", "", "", "", "", "", "", isLast, 0, ArrayList())
}









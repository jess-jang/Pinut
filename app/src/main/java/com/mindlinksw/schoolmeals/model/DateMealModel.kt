package com.mindlinksw.schoolmeals.model


data class DateMealModel(var dweek: String,
                         var month: Int,
                         var year: Int,
                         var kcal: String,
                         var mealType: String,
                         var searchDate: String,
                         var weekOfNumber: Int,
                         var menuList: ArrayList<DateMealItem>? = null)

data class DateMealItem(
        var rownum: Int,
        var menuNm: String,
        var comma: String,
        var alrgyYn: String? = "N"
)




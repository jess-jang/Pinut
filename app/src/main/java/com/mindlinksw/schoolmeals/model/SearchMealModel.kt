package com.mindlinksw.schoolmeals.model


data class SearchMealModel(var list: ArrayList<SearchMealItem>)

data class SearchMealItem(
        var rownum: String,
        var mealId: Int,
        var date: String,
        var mealType: String,
        var dDay: Int,
        var dweek: String,
        var displayMenu: String,
        var menuList: ArrayList<SearchMealMenu>)

data class SearchMealMenu(
        var rownum: Int,
        var menuNm: String,
        var alrgyYn: String,
        var searchYn: String)







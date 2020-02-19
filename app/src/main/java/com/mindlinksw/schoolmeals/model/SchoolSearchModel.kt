package com.mindlinksw.schoolmeals.model


data class SchoolSearchModel(var curPage: Int = 0,
                             var totalPage: Int = 0,
                             var fromPage: Int = 0,
                             var pageSize: Int = 0,
                             var list: ArrayList<SchoolSearchItem>? = null)

data class SchoolSearchItem(
        var rownum: String? = null,
        var insttId: Int = 0,
        var insttNm: String? = null,
        var adres: String? = null,
        var maxGrade: Int,
        var insttCode: String,
        var isChecked: Boolean
)




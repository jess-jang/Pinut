package com.mindlinksw.schoolmeals.model


data class AllergyModel(var list: ArrayList<AllergyItem>? = null)

// "rownum":순번,
// "code":알레르기 코드,
// "name":알레르기 항목,
// "targetAt":사용자 선택 여부
data class AllergyItem(
        var rownum: String? = null,
        var code: Int = 0,
        var name: String? = null,
        var targetAt: String? = null,
        var isChecked: Boolean)




package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "prsnlPolicy":개인정보 처리방침,
//    "accessTerms":이용약관
//}

data class TermModel(var prsnlPolicy: String,
                     var accessTerms: String
) : Serializable
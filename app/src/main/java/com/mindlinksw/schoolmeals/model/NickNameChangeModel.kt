package com.mindlinksw.schoolmeals.model

//{
//    "code":성공여부,
//    "nckCnt":남은 변경 횟수,
//    "error":에러코드,
//    "nckOprtnCnt":총 변경 가능 횟수
//}

data class NickNameChangeModel(
        var code: String,
        var error: String,
        var nckCnt: Int,
        var nckOprtnCnt: Int)




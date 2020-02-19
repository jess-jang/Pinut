package com.mindlinksw.schoolmeals.model


//{
//    "code":코드,
//    "marketingAlertYn":마케팅 알림 수신 여부,
//    "error":에러내용,
//    "personalAlertYn":댓글 알림 수신 여부
//}
data class PushStatusModel(
        var code: String,
        var error: String,
        var marketingAlertYn: String,
        var personalAlertYn: String
)



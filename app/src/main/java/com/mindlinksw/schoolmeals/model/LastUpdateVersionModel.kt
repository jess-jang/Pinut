package com.mindlinksw.schoolmeals.model

//{
//    "serverLastVersion":"서버에 저장되어있는 마지막 버전",
//    "forciblyVersion":"업데이트 강제 버전",
//    "adviceVersion":"업데이트 권유 버전"
//}

data class LastUpdateVersionModel(var serverLastVersion: String,
                             var forciblyVersion: String,
                             var adviceVersion: String)
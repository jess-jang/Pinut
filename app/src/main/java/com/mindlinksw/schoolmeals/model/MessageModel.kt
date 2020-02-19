package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "target": "commentInfo",
//    "boardId": "60",
//    "commentId": "79",
//    "text": "텍스트"
//    "title":"제목"
//}
data class MessageModel(
        var target: String,
        var title: String,
        var ticker: String,
        var boardId: Int,
        var commentId: Int,
        var text: String,
        var link: String
) : Serializable {

}



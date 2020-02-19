package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "youtubeMediaKey":유투브 키값,
//    "code":성공여부,
//    "error":에러코드,
//    "title":유투브 제목,
//    "url":표시될 URL,
//    "thumbnailUrl":섬네일 사진 URL
//}
data class VideoThumbnailModel(var code: String,
                               var error: String,
                               var title: String,
                               var url: String,
                               var thumbnailUrl: String,
                               var youtubeMediaKey: String
) : Serializable





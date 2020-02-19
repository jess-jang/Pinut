package com.mindlinksw.schoolmeals.model

import java.io.Serializable

//{
//    "boardStat": {
//    "delYn": "N",
//    "msg": null,
//    "delDate": null,
//    "delType": null
//},
//    "boardData": {
//    "userSocial": "facebook",
//    "nextBoardId": 200,
//    "recommendYn": "N",
//    "adContent": "기본 광고",
//    "nckNm": "닉변함",
//    "regDate": "2019.02.27",
//    "type": 1001,
//    "content": "따봉도치야 고마워",
//    "timeAgo": "1주 전",
//    "userLevel": 7,
//    "viewCnt": 37,
//    "adLink": "http://pinut.kr/",
//    "ctgryCode": "BBS_00000001",
//    "commentCnt": 7,
//    "tit": "와 개쩌는거 발견!!!",
//    "levelIcon": "https://s3.ap-northeast-2.amazonaws.com/mindlink/schoolmeal_common/profile/profile_7.png",
//    "esntlId": "CZhE1vz5mgKIAQmQPO8uy6th73zWINXnW1C/bLh7a7U=",
//    "bkmrkSttus": "N",
//    "linkYn": "N",
//    "recommendCnt": 6,
//    "userId": "CfOQiNx9obJhNpvFOBe2eQvLyo/JnNg29JDYeTo8Ghc=",
//    "prevBoardId": 203,
//    "mediaList": [
//    {
//        "sn": 0,
//        "fileUrl": "https://s3.ap-northeast-2.amazonaws.com/mindlink/schoolmeal/201902270643416980",
//        "fileContent": ""
//    }
//    ],
//    "boardId": 202,
//    "adTit": "기본 광고",
//    "updYn": "N",
//    "adThumbnailFileUrl": null
//}
//}
data class BoardDetailModel(val boardStat: BoardStatus,
                            val boardData: BoardData) : Serializable

/**
 * 게시글 상세
 */
data class BoardData(var userSocial: String,
                     var esntlId: String,
                     var recommendYn: String,
                     var bkmrkSttus: String,
                     var nckNm: String,
                     var regDate: String,
                     var recommendCnt: Int,
                     var type: Int,
                     var userId: String,
                     var content: String,
                     var timeAgo: String,
                     var viewCnt: Int,
                     var ctgryCode: String,
                     var boardId: Int,
                     var commentCnt: Int,
                     var tit: String,
                     var levelIcon: String,
                     var userLevel: Int,
                     var youtubeMediaKey: String,
                     var youtubeMediaUrl: String,
                     var thumbnailUrl: String,
                     var adThumbnailFileUrl: String,
                     var adLink: String,
                     var linkYn: String,
                     var nextBoardId: Int,
                     var prevBoardId: Int,
                     var mediaList: ArrayList<AttachItem>) : Serializable

/**
 * 이미지 아이템
 */
data class AttachItem(var sn: String,
                      var fileUrl: String,
                      var fileContent: String) : Serializable

/**
 * 게시글 상태
 */
data class BoardStatus(var delYn: String,
                       var msg: String,
                       var delDate: String,
                       var delType: String) : Serializable




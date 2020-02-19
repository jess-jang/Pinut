package com.mindlinksw.schoolmeals.manager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.mindlinksw.schoolmeals.consts.DataConst
import com.mindlinksw.schoolmeals.consts.RequestConst
import com.mindlinksw.schoolmeals.consts.SchemeConst
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.model.CommentItem
import com.mindlinksw.schoolmeals.model.NoticeItem
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.Logger
import com.mindlinksw.schoolmeals.utils.MimeTypes
import com.mindlinksw.schoolmeals.utils.TextFormatUtils
import com.mindlinksw.schoolmeals.utils.tryCatch
import com.mindlinksw.schoolmeals.view.activity.*

class SchemeManager {

    companion object {

        private val TAG: String = SchemeManager::class.java.name

        /**
         * 스킴실행
         */
        public fun run(
                activity: Activity,
                uri: Uri
        ) {
            val intent = Intent()
            intent.data = uri
            run(activity, intent)
        }

        /**
         * 스킴실행
         */
        public fun run(
                activity: Activity,
                intent: Intent
        ) {

            tryCatch {

                val runIntent = Intent()

                // intent 정보
                val action = intent.action
                val uri = intent.data
                var requestCode = -1

                Logger.e(TAG, "action : $action")

                if (uri != Uri.EMPTY && uri != null) {

                    val scheme = uri.scheme
                    val host = uri.host

                    Logger.e(TAG, "uri : $uri")
                    Logger.e(TAG, "scheme : $scheme")
                    Logger.e(TAG, "host : $host")
                    Logger.e(TAG, "action : $action")

                    // pinut 스킴 일경우
                    when (scheme) {

                        SchemeConst.SCHEME -> {

                            when (host) {

                                // 홈
                                SchemeConst.HOME -> {

                                }

                                // 학교선택
                                SchemeConst.SCHOOL_CHOOSE -> {
                                    runIntent.setClass(activity, SchoolChooseActivity::class.java)
                                }

                                // 주간식단
                                SchemeConst.WEEKLY_MEALS -> {
                                    runIntent.setClass(activity, WeeklyActivity::class.java)
                                }

                                // 게시글
                                SchemeConst.GENERAL_BOARD -> {

                                    val boardId = uri.getQueryParameter(SchemeConst.BOARD_ID)
                                    if (boardId.isNullOrEmpty()) {
                                        return
                                    }

                                    // Board Detail 값 세팅
                                    val board = BoardItem()
                                    board.boardId = boardId.toInt()

                                    // intent 데이터
                                    runIntent.setClass(activity, DetailActivity::class.java)
                                    runIntent.putExtra(BoardItem::class.java.name, board)
                                }

                                // 댓글 (게시글 실행 시키고 댓글 실행)
                                SchemeConst.GENERAL_COMMENT -> {

                                    val boardId = uri.getQueryParameter(SchemeConst.BOARD_ID)
                                    val commentId = uri.getQueryParameter(SchemeConst.COMMENT_ID)
                                    if (boardId.isNullOrEmpty() || commentId.isNullOrEmpty()) {
                                        return
                                    }

                                    // 게시글 상세
                                    val board = BoardItem()
                                    board.boardId = boardId.toInt()
                                    val detailIntent = Intent()
                                    detailIntent.data = Uri.parse("${SchemeConst.SCHEME}://${SchemeConst.GENERAL_BOARD}")
                                    detailIntent.putExtra(BoardItem::class.java.name, board)
                                    run(activity, detailIntent)

                                    // intent 데이터 (댓글)
                                    val comment = CommentItem()
                                    comment.commentId = commentId.toInt()
                                    comment.depth = 1
                                    runIntent.setClass(activity, CommentActivity::class.java)
                                    runIntent.putExtra(DataConst.TYPE, "read")
                                    runIntent.putExtra("boardId", board.boardId)
                                    runIntent.putExtra(CommentItem::class.java.name, comment)
                                }

                                // 글 작성
                                SchemeConst.GENERAL_WRITE -> {
                                    requestCode = if (SessionSingleton.getInstance(activity).isExist) {
                                        runIntent.setClass(activity, WriteActivity::class.java)
                                        RequestConst.INTENT_WRITE
                                    } else {
                                        runIntent.setClass(activity, LoginActivity::class.java)
                                        RequestConst.LOGIN_RESULT_WRITE
                                    }
                                }

                                // 비디오 리스트
                                SchemeConst.VIDEO_LIST -> {
                                    runIntent.setClass(activity, VideoActivity::class.java)
                                }

                                // 비디오 글
                                SchemeConst.VIDEO_BOARD -> {
                                    val boardId = uri.getQueryParameter(SchemeConst.BOARD_ID)
                                    if (boardId.isNullOrEmpty()) {
                                        return
                                    }

                                    val board = BoardItem()
                                    board.boardId = boardId.toInt()

                                    // intent 데이터
                                    runIntent.setClass(activity, VideoDetailActivity::class.java)
                                    runIntent.putExtra(BoardItem::class.java.name, board)
                                    requestCode = RequestConst.INTENT_DETAIL
                                }

                                // 비디오 글 작성 (비디오 리스트 실행)
                                SchemeConst.VIDEO_WRITE -> {

                                    // 비디오 리스트
                                    val listIntent = Intent()
                                    listIntent.data = Uri.parse("${SchemeConst.SCHEME}://${SchemeConst.VIDEO_LIST}")
                                    run(activity, listIntent)

                                    // 글쓰기
                                    requestCode = if (SessionSingleton.getInstance(activity).isExist) {
                                        runIntent.setClass(activity, VideoWriteActivity::class.java)
                                        runIntent.putExtra(DataConst.TYPE,
                                                RequestConst.INTENT_WRITE)
                                        runIntent.putExtra(Intent.EXTRA_TEXT,
                                                intent.getStringExtra(Intent.EXTRA_TEXT))
                                        RequestConst.INTENT_WRITE
                                    } else {
                                        runIntent.setClass(activity, LoginActivity::class.java)
                                        RequestConst.LOGIN_RESULT_WRITE
                                    }
                                }

                                // 로그인
                                SchemeConst.LOGIN -> {
                                    runIntent.setClass(activity, LoginActivity::class.java)
                                }

                                // 공지사항
                                SchemeConst.NOTICE_LIST -> {
                                    runIntent.setClass(activity, NoticeActivity::class.java)
                                }

                                // 공지사항 상세
                                SchemeConst.NOTICE_DETAIL -> {

                                    val url = uri.getQueryParameter(SchemeConst.URL)
                                    val nickName = uri.getQueryParameter(SchemeConst.NICK_NAME)
                                    val date = uri.getQueryParameter(SchemeConst.DATE)
                                    val profileImage = uri.getQueryParameter(SchemeConst.PROFILE_IMAGE)
                                    if (url.isNullOrEmpty()
                                            || profileImage.isNullOrEmpty()
                                            || nickName.isNullOrEmpty()
                                            || date.isNullOrEmpty()) {
                                        return
                                    }

                                    val model = NoticeItem(url, nickName, date, profileImage)

                                    // 공지사항에 Intent 데이터 세팅
                                    runIntent.setClass(activity, NoticeActivity::class.java)
                                    runIntent.putExtra(NoticeItem::class.java.name, model)

                                }
                            }

                            if (requestCode > -1) {
                                activity.startActivityForResult(runIntent, requestCode)
                            } else {
                                activity.startActivity(runIntent)
                            }

                            // requestCode 초기화
                            requestCode = -1

                        }

                        // 외부 URL
                        SchemeConst.HTTP, SchemeConst.HTTPS -> {
                            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    }
                } else if (!action.isNullOrEmpty()) {

                    when (action) {
                        // 텍스트 공유
                        Intent.ACTION_SEND -> {
                            val mime = intent.type
                            if (mime.isEmpty()) {
                                return
                            }

                            if (mime == MimeTypes.Text.PLAIN) {
                                setTextPlain(activity, intent)
                            }
                        }
                    }

                }

            }
        }

        /**
         * textPlain
         */
        private fun setTextPlain(
                activity: Activity,
                intent: Intent
        ) {

            tryCatch {

                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (text.isEmpty()) {
                    return
                }

                val runIntent = Intent()
                val isYoutube = TextFormatUtils.isYoutubeUrl(text)
                runIntent.data = if (isYoutube) {
                    // 동영상
                    Uri.parse("${SchemeConst.SCHEME}://${SchemeConst.VIDEO_WRITE}")
                } else {
                    Uri.parse("${SchemeConst.SCHEME}://${SchemeConst.GENERAL_WRITE}")
                }
                runIntent.putExtra(Intent.EXTRA_TEXT, text)
                run(activity, runIntent)
            }

        }

    }

}
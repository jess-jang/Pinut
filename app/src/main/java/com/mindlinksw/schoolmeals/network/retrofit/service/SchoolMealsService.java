package com.mindlinksw.schoolmeals.network.retrofit.service;


import com.mindlinksw.schoolmeals.model.AllergyModel;
import com.mindlinksw.schoolmeals.model.BannerListModel;
import com.mindlinksw.schoolmeals.model.BoardData;
import com.mindlinksw.schoolmeals.model.BoardDetailModel;
import com.mindlinksw.schoolmeals.model.BoardInsertModel;
import com.mindlinksw.schoolmeals.model.BoardModel;
import com.mindlinksw.schoolmeals.model.BookmarkModel;
import com.mindlinksw.schoolmeals.model.CommentListModel;
import com.mindlinksw.schoolmeals.model.CommentModel;
import com.mindlinksw.schoolmeals.model.DateMealModel;
import com.mindlinksw.schoolmeals.model.LastUpdateVersionModel;
import com.mindlinksw.schoolmeals.model.MainMealModel;
import com.mindlinksw.schoolmeals.model.MemberCheckModel;
import com.mindlinksw.schoolmeals.model.MemberJoinModel;
import com.mindlinksw.schoolmeals.model.MyStoryModel;
import com.mindlinksw.schoolmeals.model.NickNameChangeModel;
import com.mindlinksw.schoolmeals.model.NoAdModel;
import com.mindlinksw.schoolmeals.model.NoticeModel;
import com.mindlinksw.schoolmeals.model.NotificationModel;
import com.mindlinksw.schoolmeals.model.NotificationStatusModel;
import com.mindlinksw.schoolmeals.model.PointModel;
import com.mindlinksw.schoolmeals.model.PushStatusModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.SchoolSearchModel;
import com.mindlinksw.schoolmeals.model.SearchMealModel;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.model.TermModel;
import com.mindlinksw.schoolmeals.model.VideoThumbnailModel;
import com.mindlinksw.schoolmeals.model.WeeklyMealModel;
import com.mindlinksw.schoolmeals.model.WelcomeModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SchoolMealsService {

    /**
     * 앱 버전 확인
     */
    @GET("selectLastUpdateVersion.do")
    Call<LastUpdateVersionModel> reqLastUpdateVersion();

    /**
     * 회원가입 전 가입여부 조회
     *
     * @param socialId
     * @param socialType
     * @return
     */
    @GET("selectMberBySocialId.do")
    Call<MemberCheckModel> reqIsMemberCheck(
            @Query("socialType") String socialType,
            @Query("socialId") String socialId

    );

    /**
     * 로그인
     *
     * @param userSocial 성별 (M:남성,F:여성)
     * @param userId     소셜 Index
     */
    @GET("accessLogin.do")
    Call<SessionModel> reqLogin(
            @Query("userSocial") String userSocial,
            @Query("userId") String userId,
            @Query("emailAdres") String emailAdres
    );

    /**
     * 회원가입
     *
     * @param socialId
     * @param socialType
     * @param nckNm
     * @return
     */
    @GET("selectMberInfo.do")
    Call<MemberJoinModel> reqJoin(
            @Query("socialType") String socialType,
            @Query("socialId") String socialId
    );

    /**
     * 학교 키워드 검색
     *
     * @param insttNm  학교이름 (필수 N)
     * @param insttId  학교코드 (필수 N)
     * @param adres    주소 (필수 N)
     * @param type     학교 등급 (필수 N)
     * @param curPage  현재페이지
     * @param size     리스트에 담길 요소 개수
     * @param pageSize 화면에 표시될 페이지 개수 (1 2 3 4 5 6 7 ... 넘버링)
     */
    @GET("selectSchoolNameSpecific.do")
    Call<SchoolSearchModel> reqSchoolNameSpecific(
            @Query("insttNm") String insttNm,
            @Query("insttId") String insttId,
            @Query("adres") String adres,
            @Query("type") String type,
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 알레르기 항목조회
     */
    @GET("selectAlrgyNmList.do")
    Call<AllergyModel> reqAllergy(

    );


    /**
     * 알레르기 항목조회 - 사용자가 선택한 항목
     */
    @GET("selectAlrgyNmList.do")
    Call<AllergyModel> reqAllergy(
            @Query("targetAt") String targetAt
    );

    /**
     * 알레르기 항목 삽입
     */
    @GET("insertAlrgy.do")
    Call<ResponseModel> reqAllergyInsert(
            @Query("alrgyCode") String apiKey
    );

    /**
     * 메인화면 급식 카드 리스트
     *
     * @param insttCode - 학교코드
     * @return
     */
    @GET("selectMainMenuList.do")
    Call<MainMealModel> reqMainMealList(
            @Query("insttCode") String insttCode
    );


    /**
     * 특정날짜 메뉴조회 주단위
     *
     * @param searchDate
     * @param insttCode  - 학교코드
     * @return
     */
    @GET("selectDateMenuMltpl.do")
    Call<WeeklyMealModel> reqWeeklyMeal(
            @Query("searchDate") String searchDate,
            @Query("insttCode") String insttCode
    );

    /**
     * 특정날짜 메뉴조회 단일건
     *
     * @param searchDate
     * @param mealType
     * @param insttCode  - 학교코드
     * @return
     */
    @GET("selectDateMenuSngl.do")
    Call<DateMealModel> reqDateMeal(
            @Query("searchDate") String searchDate,
            @Query("mealType") String mealType,
            @Query("insttCode") String insttCode
    );

    /**
     * 게시판 글 리스트 조회
     *
     * @param ctgryCode
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectBbsList.do")
    Call<BoardModel> reqBoard(
            @Query("ctgryCode") String ctgryCode,
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 게시물 상세조회 V2
     *
     * @return
     */
    @GET("selectBbsInfoV2.do")
    Call<BoardDetailModel> reqBoardDetail(
            @Query("boardId") int boardId
    );

    /**
     * 게시물 삭제
     *
     * @param apiKey
     * @return
     */
    @GET("deleteBoard.do")
    Call<ResponseModel> reqBoardDelete(
            @Query("boardId") int boardId
    );


    /**
     * 글등록
     *
     * @param formData
     * @return
     */
    @POST("insertBbsArticle.do")
    Call<BoardInsertModel> reqBoardCreate(
            @Body RequestBody formData
    );

    /**
     * 게시판 글 수정
     *
     * @param formData
     * @return
     */
    @POST("updateBbsArticle.do")
    Call<BoardInsertModel> reqBoardModify(
            @Body RequestBody formData
    );

    /**
     * 특정검색어 메뉴조회 다중건
     *
     * @param searchMenu
     * @param searchCnt
     * @param insttCode  - 학교코드
     * @return
     */
    @GET("selectSearchMenuMltpl.do")
    Call<SearchMealModel> reqSearchMeal(
            @Query("searchMenu") String searchMenu,
            @Query("searchCnt") int searchCnt,
            @Query("insttCode") String insttCode
    );


    /**
     * 게시글 댓글 작성
     *
     * @param boardId
     * @param content
     * @return
     */
    @GET("insertBbsComment.do")
    Call<BoardInsertModel> reqCommentCreate(
            @Query("boardId") int boardId,
            @Query("content") String content
    );

    /**
     * 게시글 댓글조회
     *
     * @param boardId
     * @param content
     * @param replyCommentId
     * @return
     */
    @GET("insertBbsComment.do")
    Call<BoardInsertModel> reqCommentCreate(
            @Query("boardId") int boardId,
            @Query("replyCommentId") int replyCommentId,
            @Query("content") String content
    );

    /**
     * 게시글 댓글 조회
     *
     * @param boardId
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */

    @GET("selectBbsCommentList.do")
    Call<CommentListModel> reqCommentList(
            @Query("boardId") int boardId,
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 댓글 조회
     *
     * @param commentId
     * @return
     */

    @GET("selectBbsCommentInfo.do")
    Call<CommentModel> reqComment(
            @Query("commentId") int commentId
    );

    /**
     * 댓글에 달린 대댓글 리스트
     *
     * @param commentId
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectBbsReplyCommentList.do")
    Call<CommentListModel> reqCommentDetailList(
            @Query("commentId") int commentId,
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 알림 리스트 조회
     *
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectAlertList.do")
    Call<NotificationModel> reqNotificationList(
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 알림 삭제
     *
     * @param alertIds
     * @return
     */
    @GET("updateAlertSttus.do")
    Call<ResponseModel> reqDeleteNotification(
            @Query("alertIds") String alertIds
    );

    /**
     * 알림 삭제
     *
     * @return
     */
    @GET("updateAlertSttusAll.do")
    Call<ResponseModel> reqAllDeleteNotification();

    /**
     * 북마크 게시물 리스트
     *
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectBookmarkList.do")
    Call<BookmarkModel> reqBookmarkList(
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 사용자 북마크 추가
     *
     * @param boardId
     * @return
     */
    @GET("insertBkmrkInfo.do")
    Call<ResponseModel> reqInsertBookmark(
            @Query("boardId") int boardId
    );

    /**
     * 사용자 북마크 삭제
     *
     * @param boardId
     * @return
     */
    @GET("deleteBkmrkInfo.do")
    Call<ResponseModel> reqDeleteBookmark(
            @Query("boardId") int boardId
    );

    /**
     * 사용자 북마크 전체 삭제
     *
     * @param boardId
     * @return
     */
    @GET("deleteBkmrkAll.do")
    Call<ResponseModel> reqDeleteAllBookmark();


    /**
     * 내가 작성한 글 리스트
     *
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectMyBoardList.do")
    Call<MyStoryModel> reqMyStoryList(
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 게시물 좋아요 추가
     *
     * @param boardId
     * @return
     */
    @GET("insertBoardRcmnd.do")
    Call<ResponseModel> reqInsertLike(
            @Query("boardId") int boardId
    );

    /**
     * 게시물 좋아요 삭제
     *
     * @param boardId
     * @return
     */
    @GET("deleteBoardRcmnd.do")
    Call<ResponseModel> reqDeleteLike(
            @Query("boardId") int boardId
    );

    /**
     * 댓글 좋아요 추가
     *
     * @param commentId
     * @return
     */
    @GET("insertCommentRcmnd.do")
    Call<ResponseModel> reqInsertCommentLike(
            @Query("commentId") int commentId
    );

    /**
     * 댓글 좋아요, 삭제
     *
     * @param commentId
     * @return
     */
    @GET("deleteCommentRcmnd.do")
    Call<ResponseModel> reqDeleteCommentLike(
            @Query("commentId") int commentId
    );

    /**
     * 댓글 삭제
     *
     * @param commentId
     * @return
     */
    @GET("deleteComment.do")
    Call<ResponseModel> reqDeleteComment(
            @Query("commentId") int commentId
    );

    /**
     * 댓글 수정
     *
     * @param commentId
     * @return
     */
    @GET("updateBbsComment.do")
    Call<ResponseModel> reqModifyComment(
            @Query("commentId") int commentId,
            @Query("content") String content
    );

    /**
     * 본인 정보 조회
     *
     * @return
     */
    @GET("selectMberInfo.do")
    Call<SessionModel> reqMember();

    /**
     * 스토어 버전 정보
     *
     * @return
     */
    @GET("store/apps/details")
    Call<String> reqStoreVersion(
            @Query("id") String id
    );

    /**
     * 닉네임 정보 수정 전 닉네임 확인
     *
     * @param nckNm
     * @return
     */
    @GET("updatePrevMberNckNm.do")
    Call<NickNameChangeModel> reqDuplicateNickName(
            @Query("nckNm") String nckNm
    );

    /**
     * 닉네임 수정
     *
     * @param nckNm
     * @param regYn 회원가입 프로세스 여부
     * @return
     */
    @GET("updateMberNckNm.do")
    Call<ResponseModel> reqUpdateNickName(
            @Query("nckNm") String nckNm,
            @Query("regYn") String regYn
    );

    /**
     * 개인정보, 이용약관
     *
     * @return
     */
    @GET("selectMberPolicy.do")
    Call<TermModel> reqTerm();

    /**
     * 공지사항 리스트
     *
     * @param curPage
     * @param size
     * @param pageSize
     * @return
     */
    @GET("selectNoticeList.do")
    Call<NoticeModel> reqNotice(
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 회원 정보 수정, 학교 학년
     *
     * @return
     */
    @GET("updateMberInfo.do")
    Call<ResponseModel> reqUpdateSchool(
            @Query("insttCode") String insttCode,
            @Query("yearly") String yearly
    );

    /**
     * 마케팅, 댓글 푸쉬 여부 수정
     *
     * @return
     */
    @GET("updateMberInfo.do")
    Call<ResponseModel> reqUpdatePushStatus(
            @Query("marketingAlertYn") String marketingAlertYn,
            @Query("personalAlertYn") String personalAlertYn
    );


    /**
     * 상단 배너 리스트
     *
     * @return
     */
    @GET("selectTopAdList.do")
    Call<BannerListModel> reqBannerList();

    /**
     * 포인트 내역 리스트 조회
     *
     * @return
     */
    @GET("selectPointList.do")
    Call<PointModel> reqPointList(
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize
    );

    /**
     * 사용자 알림 조회
     */
    @GET("selectMberAlertSttus.do")
    Call<NotificationStatusModel> reqNotificationStatus();

    /**
     * 비디오 글 리스트 조회
     *
     * @return
     */
    @GET("selectBbsVideoList.do")
    Call<BoardModel> reqVideoList(
            @Query("ctgryCode") String ctgryCode,
            @Query("curPage") int curPage,
            @Query("size") int size,
            @Query("pageSize") int pageSize,
            @Query("orderType") String orderType
    );


    /**
     * 비디오 상세조회
     *
     * @param boardId
     * @return
     */
    @GET("selectBbsVideoInfo.do")
    Call<BoardData> reqVideoDetail(
            @Query("boardId") int boardId
    );

    /**
     * 비디오 글 작성 섬네일 정보 추출
     *
     * @param youtubeMediaUrl
     * @return
     */
    @GET("selectYoutubeSumbnailInfo.do")
    Call<VideoThumbnailModel> reqVideoThumbnail(
            @Query("youtubeMediaUrl") String youtubeMediaUrl
    );

    /**
     * 비디오 글 삽입
     *
     * @param ctgryCode
     * @param tit
     * @param youtubeMediaUrl
     * @return
     */
    @GET("insertBbsVideoArticle.do")
    Call<BoardInsertModel> reqVideoCreate(
            @Query("ctgryCode") String ctgryCode,
            @Query("tit") String tit,
            @Query("youtubeMediaUrl") String youtubeMediaUrl
    );

    /**
     * 게시판 글 수정
     *
     * @param formData
     * @return
     */
    @POST("updateBbsVideoArticle.do")
    Call<BoardInsertModel> reqVideoModify(
            @Query("boardId") String ctgryCode,
            @Query("tit") String tit,
            @Query("youtubeMediaUrl") String youtubeMediaUrl
    );

    /**
     * FCM - Token 등록
     */
    @GET("registMberPushToken.do")
    Call<ResponseModel> reqFCMRegistToken(
            @Query("token") String token
    );

    /**
     * FCM - Token 삭제
     */
    @GET("deleteMberPushToken.do")
    Call<ResponseModel> reqFCMDeleteToken();

    /**
     * 사용자 마케팅, 댓글 알림 수신 여부 조회
     */
    @GET("selectMberPushSttus.do")
    Call<PushStatusModel> reqPushStatus();

    /**
     * 광고 대체 출력
     */
    @GET("getNoAdSelfImage.do")
    Call<NoAdModel> reqNoAd();

    /**
     * 웰컴팝업 리스트
     */
    @GET("selectWelcomePopAdList.do")
    Observable<WelcomeModel> reqWelcomeList();

}

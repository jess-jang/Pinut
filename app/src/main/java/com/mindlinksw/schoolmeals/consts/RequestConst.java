package com.mindlinksw.schoolmeals.consts;

import com.facebook.internal.CallbackManagerImpl;


public @interface RequestConst {

    int INTENT_WRITE = 1001;
    int INTENT_MODIFY = 1002;
    int INTENT_DETAIL = 1003; // 홈 > 디데일 > 홈
    int INTENT_DETAIL_DELETE = 1013; // 디테일 > 삭제
    int INTENT_DETAIL_UPDATE = 1023; // 디테일 > 수정
    int INTENT_TERM = 1004;
    int INTENT_NICKNAME = 1005; // 닉네임 변경
    int INTENT_SCHOOL = 1006; // 학교선택
    int INTENT_MYPAGE = 1008; // 마이페이지

    int LOGIN_REDIRECT = 2000;
    int LOGIN_FACEBOOK = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
    int LOGIN_GOOGLE = 2004;
    int LOGIN_RESULT_WRITE = 2005;
    int LOGIN_RESULT_NOTIFICATION = 2010;

    int GALLERY = 3001;
    int SERVER = 3003;

    int REFRESH = 4000;

}

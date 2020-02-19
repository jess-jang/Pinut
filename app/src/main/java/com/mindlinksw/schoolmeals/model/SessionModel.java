package com.mindlinksw.schoolmeals.model;

import java.io.Serializable;

//{
//        "userSocial":유저 소셜 구분,
//        "userNm":사용자 이름(AES256 암호화),
//        "nextLevelExp":필요 경험치,
//        "nckNm":닉네임,
//        "regDate":가입날짜,
//        "emailAdres":이메일,
//        "mbtlnum":휴대전화번호(AES256 암호화_,
//        "insttCode":학교 코드,
//        "expPoint":현재 경험치,
//        "adres":주소,
//        "prflPhotoUrl":프로필 사진 URL (사용X),
//        "levelIcon":레벨 아이콘,
//        "zip":우편번호,
//        "userPoint":포인트 점수,
//        "insttNm":학교 이름,
//        "esntlId":유저 구분 아이디 (AES256 암호화),
//        "level":유저 레벨,
//        "expPer":유저 경험치 퍼센트,
//        "userId":사용자 소셜 구분 아이디 (AES256 암호화),
//        "adresDetail":상세 주소,
//        "brthdy":생년월일,
//        "nckOprtnCnt":남은 닉네임 변경 횟수,
//        "sexdstnCode":성별,
//        "location":지역,
//        "yearly":학년,
//        "age":연령대
//}

public class SessionModel implements Serializable {

    public String code;
    public String error;
    public String userSocial;
    public String zip;
    public String userNm;
    public String insttNm;
    public String esntlId;
    public String nckNm;
    public String regDate;
    public String emailAdres;
    public String userId;
    public String adresDetail;
    public String brthdy;
    public String mbtlnum;
    public int nckOprtnCnt;
    public String insttCode;
    public String sexdstnCode;
    public String location;
    public String adres;
    public int yearly;
    public String prflPhotoUrl;
    public String age;
    public int userPoint;
    public String levelIcon;
    public int level;
    public int nextLevelExp;
    public int expPoint;
    public int expPer;

    public String getUserSocial() {
        return userSocial;
    }

    public String getInsttNm() {
        return insttNm;
    }

    public void setInsttNm(String insttNm) {
        this.insttNm = insttNm;
    }

    public String getEsntlId() {
        return esntlId;
    }

    public String getNckNm() {
        return nckNm;
    }

    public void setNckNm(String nckNm) {
        this.nckNm = nckNm;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInsttCode() {
        return insttCode;
    }

    public void setInsttCode(String insttCode) {
        this.insttCode = insttCode;
    }

    public int getYearly() {
        return yearly;
    }

    public int getUserPoint() {
        return userPoint;
    }

    public String getLevelIcon() {
        return levelIcon;
    }

    public void setLevelIcon(String levelIcon) {
        this.levelIcon = levelIcon;
    }

    public int getLevel() {
        return level;
    }

    public int getNextLevelExp() {
        return nextLevelExp;
    }

    public int getExpPoint() {
        return expPoint;
    }

    public int getExpPer() {
        return expPer;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }
}

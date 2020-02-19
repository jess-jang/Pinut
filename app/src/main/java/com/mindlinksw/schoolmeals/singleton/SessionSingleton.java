package com.mindlinksw.schoolmeals.singleton;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.security.AES256Util;

import java.net.URLEncoder;

/**
 * Created by interpark on 2018. 6. 11..
 */

public class SessionSingleton {

    private static SessionSingleton mInstance = null;
    private Context mContext;

    private SessionSingleton(Context context) {
        this.mContext = context;
    }

    public static synchronized SessionSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionSingleton(context);
        }

        return mInstance;
    }

    /**
     * 멤버 정보 세팅
     *
     * @param session
     */
    public void insert(SessionModel session) {

        try {

            // 암호화 실시후 저장
            Gson gson = new Gson();
            String data = AES256Util.encode(mContext.getString(R.string.aes256_key), gson.toJson(session));

            SharedPreferencesUtils.save(mContext, SessionModel.class.getName(), data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 멤버 정보 제거
     */
    public void delete() {

        try {

            SharedPreferencesUtils.remove(mContext, SessionModel.class.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 멤버 정보 가져옴
     */
    public SessionModel select() {

        try {

            if (isExist()) {
                String data = AES256Util.decode(mContext.getString(R.string.aes256_key), SharedPreferencesUtils.read(mContext, SessionModel.class.getName(), null));
                Gson gson = new Gson();
                return gson.fromJson(data, SessionModel.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 멤버 정보 가져옴
     */
    public String getHeader() {

        try {

            if (isExist()) {

                SessionModel session = select();

                JsonObject object = new JsonObject();
                object.addProperty("esntlId", session.getEsntlId());
                object.addProperty("userId", session.getUserId());
                object.addProperty("userSocial", session.getUserSocial());
                return URLEncoder.encode(object.toString(), "utf-8");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 멤버 정보 유무
     */
    public boolean isExist() {

        try {
            return SharedPreferencesUtils.read(mContext, SessionModel.class.getName(), null) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * API의 데이터를 비교하여 자기 자신인지 체크
     */
    public boolean isSelf(String esntlId) {

        try {

            if (ObjectUtils.isEmpty(select())) {
                return false;
            }

            if (!ObjectUtils.isEmpty(esntlId) && select().getEsntlId().equals(esntlId)) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 학교 이름
     */
    public String getInsttNm() {

        String result = null;

        try {
            // 학교 코드
            if (isExist()) {
                result = select().getInsttNm();
            } else {
                result = SharedPreferencesUtils.read(mContext, DataConst.NONMEMBER_SCHOOL_NAME, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 학교 코드
     */
    public String getInsttCode() {

        String result = null;

        try {
            // 학교 코드
            if (isExist()) {
                result = select().getInsttCode();
            } else {
                result = SharedPreferencesUtils.read(mContext, DataConst.NONMEMBER_SCHOOL_CODE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}

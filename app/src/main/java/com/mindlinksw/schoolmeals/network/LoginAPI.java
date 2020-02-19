package com.mindlinksw.schoolmeals.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.interfaces.OnLoginResponseListener;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;
import com.mindlinksw.schoolmeals.view.activity.MainActivity;
import com.mindlinksw.security.AES256Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAPI {

    public static final String TAG = DetailActivity.class.getName();

    /**
     * 멤버 로그인
     *
     * @param socialType
     * @param userId
     */
    public static void access(Context context, String socialType, String userId, String email, final OnLoginResponseListener listener) {

        try {

            Logger.e(TAG, " --- login request ---------------------------------- ");
            Logger.e(TAG, socialType);
            Logger.e(TAG, userId + " / " + AES256Util.decode(context.getString(R.string.aes256_key), userId));
            Logger.e(TAG, email);
            Logger.e(TAG, " --- login request ---------------------------------- ");

            if (ObjectUtils.isEmpty(socialType)
                    || ObjectUtils.isEmpty(userId)
                    || ObjectUtils.isEmpty(email)) {
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());
            Call<SessionModel> call = service.reqLogin(
                    socialType,
                    userId,
                    email);

            RetrofitCall.enqueueWithRetry(call, new Callback<SessionModel>() {

                @Override
                public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {

                    if (listener == null) {
                        return;
                    }

                    try {

                        if (response.isSuccessful()) {

                            SessionModel resData = (SessionModel) response.body();

                            if ("cancel".equals(resData.getCode())) {
                                listener.onFail(resData.getError());
                            } else {
                                if (resData.getUserSocial() != null) {
                                    listener.onSuccess(resData);
                                } else {
                                    listener.onFail(resData.getError());
                                }
                            }

                            Logger.e(TAG, " --- login response ---------------------------------- ");
                            Logger.e(TAG, new Gson().toJson(resData));
                            Logger.e(TAG, " --- login response ---------------------------------- ");

                        } else {
                            listener.onFail("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFail("");
                    }

                }

                @Override
                public void onFailure(Call<SessionModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    if (listener != null) {
                        listener.onFail("");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail(null);
            }
        }
    }

    /**
     * 로그아웃
     *
     * @param activity
     */
    public static void logout(final Activity activity) {


        try {

            // FCM
            FCMAPI.deleteToken(activity, new OnResponseListener() {
                @Override
                public void onSuccess() {

                    try {

                        // 회원정보 삭제
                        SessionSingleton.getInstance(activity).delete();

                        // 모든 앱 종료
                        ActivityCompat.finishAffinity(activity);

                        // 메인 실행
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String error) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(activity.findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

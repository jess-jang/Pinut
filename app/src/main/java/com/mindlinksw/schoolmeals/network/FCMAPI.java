package com.mindlinksw.schoolmeals.network;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMAPI {

    public static final String TAG = DetailActivity.class.getName();

    /**
     * FCM Register
     *
     * @param context
     * @param listener
     */
    public static void registerToken(final Context context, final OnResponseListener listener) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Logger.INSTANCE.w(TAG, "getInstanceId failed : " + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        if (Utils.isTest()) {

//                            Toast.makeText(context, "token을 저장하였습니다.", Toast.LENGTH_SHORT).show();
                        }

                        if (ObjectUtils.isEmpty(token)) {
                            return;
                        }

                        /**
                         * Regist Token
                         */
                        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

                        Call<ResponseModel> call = service.reqFCMRegistToken(token);
                        RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                                try {

                                    if (response.isSuccessful()) {
                                        ResponseModel resData = (ResponseModel) response.body();

                                        if (resData != null) {
                                            if (listener != null) {
                                                if ("success".equals(resData.getCode())) {
                                                    listener.onSuccess();
                                                } else {
                                                    listener.onFail(resData.getError());
                                                }
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (listener != null) {
                                        listener.onFail(null);
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                Logger.e(TAG, t.getMessage());
                                if (listener != null) {
                                    listener.onFail(null);
                                }
                            }
                        });
                    }
                });
    }

    /**
     * FCM Delete
     *
     * @param context
     * @param listener
     */
    public static void deleteToken(final Context context, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call = service.reqFCMDeleteToken();
        RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                try {

                    if (response.isSuccessful()) {

                        ResponseModel resData = (ResponseModel) response.body();

                        if (resData != null) {

                            if (listener != null) {
                                if ("success".equals(resData.getCode())) {
                                    listener.onSuccess();
                                    // fcm 인스턴스 삭제
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                } else {
                                    listener.onFail(resData.getError());
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFail(null);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Logger.e(TAG, t.getMessage());
                if (listener != null) {
                    listener.onFail(null);
                }
            }
        });

    }

}

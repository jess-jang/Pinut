package com.mindlinksw.schoolmeals.network;

import android.content.Context;

import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.interfaces.OnNoAdListener;
import com.mindlinksw.schoolmeals.model.NoAdModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoAdAPI {

    public static final String TAG = DetailActivity.class.getName();

    /**
     * 광고 대체 이미지
     * @param context
     * @param listener
     */
    public static void get(Context context, final OnNoAdListener listener) {

        try {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());
            Call<NoAdModel> call = service.reqNoAd();

            if (listener == null) {
                return;
            }

            RetrofitCall.enqueueWithRetry(call, new Callback<NoAdModel>() {

                @Override
                public void onResponse(Call<NoAdModel> call, Response<NoAdModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            NoAdModel resData = (NoAdModel) response.body();

                            if (resData != null) {
                                listener.onSuccess(resData.getAdMap());
                            } else {
                                listener.onFail();
                            }

                        } else {
                            listener.onFail();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFail();
                    }

                }

                @Override
                public void onFailure(Call<NoAdModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    listener.onFail();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail();
        }
    }
}

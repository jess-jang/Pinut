package com.mindlinksw.schoolmeals.network;

import android.content.Context;

import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardAPI {

    public static final String TAG = DetailActivity.class.getName();

    /**
     * 좋아요
     *
     * @param context
     * @param isLike
     * @param boardId
     * @param listener
     */
    public static void setLike(Context context, boolean isLike, int boardId, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call;
        if (isLike) {
            call = service.reqInsertLike(boardId);
        } else {
            call = service.reqDeleteLike(boardId);
        }

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

    /**
     * 북마크
     *
     * @param context
     * @param isBookmark
     * @param boardId
     * @param listener
     */
    public static void setBookmark(Context context, boolean isBookmark, int boardId, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call;
        if (isBookmark) {
            call = service.reqInsertBookmark(boardId);
        } else {
            call = service.reqDeleteBookmark(boardId);
        }

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

}

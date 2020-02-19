package com.mindlinksw.schoolmeals.network;

import android.content.Context;

import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.BoardInsertModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAPI {

    public static final String TAG = DetailActivity.class.getName();

    /**
     * 댓글 달기
     */
    public static void setWrite(Context context, int boardId, int replyCommentId, String content, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<BoardInsertModel> call = service.reqCommentCreate(
                boardId,
                replyCommentId,
                content);

        RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

            @Override
            public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                try {

                    if (response.isSuccessful()) {
                        BoardInsertModel resData = (BoardInsertModel) response.body();

                        if (resData != null) {
                            Logger.e(TAG, resData.toString());
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
            public void onFailure(Call<BoardInsertModel> call, Throwable t) {
                Logger.e(TAG, t.getMessage());
                if (listener != null) {
                    listener.onFail(null);
                }
            }
        });

    }

    /**
     * 댓글 수정
     */
    public static void setModify(Context context, final int commentId, final String content, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call = service.reqModifyComment(commentId, content);
        RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                try {

                    if (response.isSuccessful()) {
                        ResponseModel resData = (ResponseModel) response.body();

                        if (resData != null) {
                            Logger.e(TAG, resData.toString());
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
                    Logger.e(TAG, e.getMessage());
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
     * 댓글 삭제
     */
    public static void setDelete(Context context, final int commentId, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call = service.reqDeleteComment(commentId);
        RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                try {

                    if (response.isSuccessful()) {
                        ResponseModel resData = (ResponseModel) response.body();

                        if (resData != null) {
                            Logger.e(TAG, resData.toString());
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
                    Logger.e(TAG, e.getMessage());
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
     * 좋아요
     */
    public static void setLike(Context context, boolean isLike, int commentId, final OnResponseListener listener) {

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(context, SchoolMealsService.class, HostConst.apiHost());

        Call<ResponseModel> call;
        if (isLike) {
            call = service.reqInsertCommentLike(commentId);
        } else {
            call = service.reqDeleteCommentLike(commentId);
        }

        RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                try {

                    if (response.isSuccessful()) {
                        ResponseModel resData = (ResponseModel) response.body();

                        if (resData != null) {
                            Logger.e(TAG, resData.toString());
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

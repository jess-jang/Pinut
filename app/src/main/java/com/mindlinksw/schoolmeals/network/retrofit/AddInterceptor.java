package com.mindlinksw.schoolmeals.network.retrofit;

import android.content.Context;

import com.mindlinksw.schoolmeals.BuildConfig;
import com.mindlinksw.schoolmeals.consts.APIConst;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by N16326 on 2018. 10. 17..
 */

public class AddInterceptor implements Interceptor {

    private Context mContext;

    public AddInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("apiKey", APIConst.INSTANCE.apkKey());
        builder.addHeader("appInfo", Utils.getAppInfo());

        if (SessionSingleton.getInstance(mContext).isExist()) {
            String userInfo = SessionSingleton.getInstance(mContext).getHeader();
            builder.addHeader("userInfo", userInfo != null ? userInfo : "");

            if (BuildConfig.DEBUG) {
                Logger.e("OkHttp", "userInfo : " + userInfo);
            }
        }

        Response response = chain.proceed(builder.build());

        return response;
    }
}
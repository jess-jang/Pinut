package com.mindlinksw.schoolmeals.network.retrofit;

import android.content.Context;

import com.mindlinksw.schoolmeals.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitRequest {

    //JSON
    public static <T> T createRetrofitJSONService(Context context, final Class<T> classes, final String url) {

        // 로그
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        okHttpClient.addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new AddInterceptor(context));

        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .client(okHttpClient.build())
                .build();

        T service = retrofit.create(classes);

        return service;
    }

    // ScalarsConverterFactory for String
    public static <T> T createRetrofitScalarsService(Context context, final Class<T> classes, final String url) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        okHttpClient.interceptors().add(new AddInterceptor(context));

        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(url)
                .client(okHttpClient.build())
                .build();

        T service = retrofit.create(classes);

        return service;
    }

}

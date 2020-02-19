
package com.mindlinksw.schoolmeals.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.auth.KakaoSDK;
import com.mindlinksw.schoolmeals.service.FCMService;

import io.fabric.sdk.android.Fabric;

public class PinutApplication extends Application {

    public static volatile PinutApplication mInstance;

    /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static PinutApplication getContext() {
        if (mInstance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.PinutApplication");
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // 카카오
        KakaoSDK.init(new KakaoSDKAdapter());

        // 알림
        initNotification();

        // FCM
        FirebaseMessaging.getInstance().subscribeToTopic("ALL");

        // Fabric
        Fabric.with(this, new Crashlytics());

    }

    /**
     * 알림
     */
    private void initNotification() {

        try {
            // 알림채널
            FCMService.setNotificationChannel(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

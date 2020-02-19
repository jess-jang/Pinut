package com.mindlinksw.schoolmeals.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.FCMConst;
import com.mindlinksw.schoolmeals.consts.SchemeConst;
import com.mindlinksw.schoolmeals.model.MessageModel;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.view.activity.IntroActivity;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        Logger.INSTANCE.d(TAG, "Refreshed token: " + token);
    }

    /**
     * Notification 처리
     *
     * @param remoteMessage
     */
    private void sendNotification(RemoteMessage remoteMessage) {

        try {

            Map<String, String> data = remoteMessage.getData();
            String message = URLDecoder.decode(data.get("message"), "utf-8");
            Logger.e(TAG, message);

            // Data Model
            MessageModel model = null;
            if (!ObjectUtils.isEmpty(message)) {
                JsonReader reader = new JsonReader(new StringReader(message));
                reader.setLenient(true);

                Gson gson = new Gson();
                model = gson.fromJson(reader, MessageModel.class);
            }

            if (ObjectUtils.isEmpty(model)) {
                return;
            }

            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setData(Uri.parse(model.getLink()));

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, FCMConst.CHANNEL_ALL)
                            .setSmallIcon(R.mipmap.ic_ticker)
                            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setTicker(!ObjectUtils.isEmpty(model.getTicker()) ? model.getTicker() : getString(R.string.notification_default_ticker))
                            .setContentTitle(!ObjectUtils.isEmpty(model.getTitle()) ? model.getTitle() : getString(R.string.notification_default_title))
                            .setContentText(!ObjectUtils.isEmpty(model.getText()) ? model.getText() : getString(R.string.notification_default_content));

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            long time = System.currentTimeMillis();
            int pushId = (int) (time / 1000);
            notificationManager.notify(pushId, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notification Channel
     */
    public static void setNotificationChannel(Context context) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Channel Create
                NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(FCMConst.GROUP_GENERAL, context.getString(R.string.notification_group_name));
                manager.createNotificationChannelGroup(notificationChannelGroup);

                // Channel Setting
                NotificationChannel channel = new NotificationChannel(FCMConst.CHANNEL_ALL, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
                channel.setGroup(FCMConst.GROUP_GENERAL);
                channel.setLightColor(ContextCompat.getColor(context, R.color.colorAccent));
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

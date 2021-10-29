package com.example.androidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    public PushNotificationService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }


    //    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
//        String title = remoteMessage.getNotification().getTitle();
//        String text = remoteMessage.getNotification().getBody();
//        final String CHANNEL_ID = "HEAD_UP_NOTIFICATION";
//        NotificationChannel channel = new NotificationChannel(
//                CHANNEL_ID,
//                "Heads up Notification",
//                NotificationManager.IMPORTANCE_HIGH
//        );
//        getSystemService(NotificationManager.class).createNotificationChannel(channel);
//        NotificationManager
//        super.onMessageReceived(remoteMessage);
//    }

}

package com.example.androidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService" ;

    public PushNotificationService() {

    }

    // Methods taken from : https://stackoverflow.com/a/41515597

    @Override
    public void onNewToken(@NonNull String s) {
        // Get updated InstanceID token.
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

//        @Override
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

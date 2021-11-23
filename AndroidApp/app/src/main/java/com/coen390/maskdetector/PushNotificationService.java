package com.coen390.maskdetector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.appwrite.Client;


public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService" ;

    public PushNotificationService() {
    }

    private String defaultChannel = "defaultChannel";

    // Methods taken from : https://stackoverflow.com/a/41515597

    @Override
    public void onNewToken(@NonNull String appToken) {
        // Get updated InstanceID token.
        super.onNewToken(appToken);
        Log.d(TAG, "Refreshed token: " + appToken);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", appToken).apply();

        Client client = new Client(getApplicationContext())
                .setEndpoint("https://appwrite.orpine.net/v1") // Your API Endpoint
                .setProject("6137a2ef0d4f5"); // Your project ID
    }

    @RequiresApi(api = Build.VERSION_CODES.O) // TODO- ensure this doesn't mess shit up
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification() == null){
            Map<String, String> data = remoteMessage.getData();

            Log.d(TAG, "Data Received: " + data.get("Title") + " and body: "+ data.get("Body"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(this, defaultChannel)
                    .setContentTitle(data.get("Title"))
                    .setContentText(data.get("Body"))
                    .setSmallIcon(R.mipmap.ic_launcher_icon) // TODO - Icon not showing properly???
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify( 1, notification); // TODO - Ensure that hard coded ID doesn't mess anything

        }else{
            super.onMessageReceived(remoteMessage);
        }
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }


}

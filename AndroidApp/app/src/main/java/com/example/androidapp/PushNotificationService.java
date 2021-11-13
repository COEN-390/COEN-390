package com.example.androidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.*;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Functions;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService" ;

    public PushNotificationService() {
    }

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

        Functions functions = new Functions(client);

        try {
            functions.createExecution(
                    "61901e7628bd2",
                    "",
                    new Continuation<Object>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object o) {
                            System.out.println("Creating Session");
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                }
                            } catch (AppwriteException e){
                                System.out.println("createSession() " + new Timestamp(System.currentTimeMillis()));
                                System.out.println(e.getMessage());
                                System.out.println(e.getCode());
                                System.out.println(e.getResponse());
                            } catch (Throwable th) {
                                Log.e("ERROR", "Unable to create session");
                            }
                        }
                    }

            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }


}

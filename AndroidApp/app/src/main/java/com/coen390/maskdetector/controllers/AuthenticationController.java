package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.coen390.maskdetector.LoginActivity;
import com.coen390.maskdetector.MainActivity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import io.appwrite.services.Database;
import io.appwrite.services.Functions;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

public class AuthenticationController {
    private Context context;
    private Client client;
    private Account account;
    private Database db;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private static final String TAG = "AuthenticationController";

    public AuthenticationController(Context context) {
        this.context = context;
        sharedPreferencesHelper = new SharedPreferencesHelper(context);

        // Initialize Appwrite SDK
        this.client = AppwriteController.getClient(context);
        this.account = new Account(this.client);
        this.db = new Database(this.client);
    }

    public void createSession(String email, String password) throws AppwriteException {
        // Create the session
        account.createSession(email, password, new Continuation<Object>() {
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
                        getAccount();
                        subscribeToken();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (AppwriteException e) {
                    System.out.println("createSession() " + new Timestamp(System.currentTimeMillis()));
                    System.out.println(e.getMessage());
                    System.out.println(e.getCode());
                    System.out.println(e.getResponse());
                } catch (Throwable th) {
                    System.out.println("test");
                    System.out.println(th.getMessage());
                    Log.e("ERROR", "Unable to create session");
                }
            }
        });
    }

    public void endSession() {
        try {
            account.deleteSession("current", new Continuation<Object>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    try {
                        if (o instanceof Result.Failure) {
                            Result.Failure failure = (Result.Failure) o;
                            throw failure.exception;
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (AppwriteException e) {
                        System.out.println("endSession() " + new Timestamp(System.currentTimeMillis()));
                        System.out.println(e.getMessage());
                        System.out.println(e.getCode());
                        System.out.println(e.getResponse());
                    } catch (Throwable th) {
                        Log.e("ERROR", "Unable to end session");
                    }
                }
            });
        } catch (AppwriteException e) {
            System.out.println("endSession() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    private void getAccount() {
        try {
            account.get(new Continuation<Object>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    String json = "";
                    try {
                        if (o instanceof Result.Failure) {
                            Result.Failure failure = (Result.Failure) o;
                            throw failure.exception;
                        } else {
                            Response response = (Response) o;
                            json = response.body().string();
                            sharedPreferencesHelper.setUser(json);
                        }
                    } catch (AppwriteException e) {
                        System.out.println("storeUsername() " + new Timestamp(System.currentTimeMillis()));
                        System.out.println(e.getMessage());
                        System.out.println(e.getCode());
                        System.out.println(e.getResponse());
                        Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    } catch (Throwable th) {
                        Log.e("ERROR", "Unable to get user");
                    }
                }
            });
        } catch (AppwriteException e) {
            System.out.println("storeUsername() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    /**
     * Method used to obtain token for app Taken from:
     * https://stackoverflow.com/a/66696714
     */

    private void tokenCall() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();

                Log.d(TAG, "Firebase Cloud Messaging token: " + token);

            }
        });
    }

    private void subscribeToken() {
        Functions functions = new Functions(client);

        try {
            functions.createExecution("61901e7628bd2", PushNotificationService.getToken(context),
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
                            } catch (AppwriteException e) {
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
}

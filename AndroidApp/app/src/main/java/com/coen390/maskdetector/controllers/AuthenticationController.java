package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.coen390.maskdetector.LoginActivity;
import com.coen390.maskdetector.MainActivity;
import com.coen390.maskdetector.PushNotificationService;
import com.coen390.maskdetector.UsersActivity;
import com.coen390.maskdetector.UsersRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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
    private final int[] zzz = {0}; //to sync account creation threads :)
    private UsersController usersController;

    public AuthenticationController(Context context) {
        this.context = context;
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
        usersController = new UsersController(context);

        // Initialize Appwrite SDK
        this.client = AppwriteController.getClient(context);
        this.account = new Account(this.client);
        this.db = new Database(this.client);
    }

    public String createUser(String email, String password, String name) throws AppwriteException, JSONException {
        final String[] result = {"ERROR: SOMETHING WENT WRONG"};
        zzz[0] = 0;

        account.create(
                email,
                password,
                name,
                new Continuation<Object>() {
                    @NotNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NotNull Object o) {
                        String json = "";
                        System.out.println("Creating User...");
                        try {
                            if (o instanceof Result.Failure) {
                                Result.Failure failure = (Result.Failure) o;
                                throw failure.exception;
                            } else {
                                Response response = (Response) o;
                                json = response.body().string();
                                result[0] = "Account Created Successfully!";
                            }
                        }  catch (AppwriteException e) {
                            System.out.println("setUserLevel() " + new Timestamp(System.currentTimeMillis()));
                            System.out.println(e.getMessage());
                            System.out.println(e.getCode());
                            System.out.println(e.getResponse());
                            result[0] = "Could not create account!";
                        } catch (Throwable th) {
                            System.out.println("test");
                            System.out.println(th.getMessage());
                            Log.e("ERROR", "Unable to create session");
                            result[0] = "Could not create account!";
                        } finally {
                            zzz[0]++;
                        }
                    }
                }
        );

        usersController.createUser(name, email, "id", "user"); //TODO: setup organization ID

        System.out.println("User should be done now");
        return result[0];
    }

    public String createAdmin(String email, String password) throws AppwriteException, JSONException {

        final String[] result = {"ERROR: SOMETHING WENT WRONG"};
        zzz[0] = 0;

        account.create(
                email,
                password,
                new Continuation<Object>() {
                    @NotNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NotNull Object o) {
                        String json = "";
                        System.out.println("Creating User...");
                        try {
                            if (o instanceof Result.Failure) {
                                Result.Failure failure = (Result.Failure) o;
                                throw failure.exception;
                            } else {
                                Response response = (Response) o;
                                json = response.body().string();
                                result[0] = "Account Created Successfully!";
                            }
                        } catch (Throwable th) {
                            Log.e("ERROR", th.toString());
                            result[0] = "Error during account creation :(";
                        } finally {
                            zzz[0]++;
                        }
                    }
                }
        );
        while (true){
            if (zzz[0] == 1){
                createSession(email, password);
                break;
            }
        }
        while (true){
            if (zzz[0] == 3){
                setUserLevel("admin");
                break;
            }
        }
        while (true){
            if (zzz[0] == 4){
                getAccount();
                break;
            }
        }
        while (true){
            if (zzz[0] == 5){
                break;
            }
        }

        usersController.createUser("admin", email, "id", "admin"); //TODO: setup organization ID

        System.out.println("User should be done now");

        
        return result[0];
    }

    /**
     * Method used to delete an existing user from the Appwrite Users list
     * @return Result of the operation (Success or failure)
     * @throws AppwriteException
     * @throws JSONException
     */
    public String deleteUser(String email) throws AppwriteException, JSONException {
        final String[] result = {"ERROR: SOMETHING WENT WRONG"};
        zzz[0] = 0;

        account.delete(new Continuation<Object>() {
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
                        result[0] = "Account Deleted Successfully!";
                    }
                } catch (Throwable th) {
                Log.e("ERROR", th.toString());
                }
            }
        });

        usersController.deleteUserFromCollection(email);

        System.out.println("User should be deleted now");
        return result[0];
    }

    public int setUserLevel(String x) throws AppwriteException, JSONException {
        JSONObject pref = new JSONObject("{'userType':'" + x + "'}");
        account.updatePrefs(
                pref,
                new Continuation<Object>() {
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
                                System.out.println("setUserLevel() has returned: " + json);
                                sharedPreferencesHelper.setUser(json);
                            }
                        } catch (AppwriteException e) {
                            System.out.println("setUserLevel() " + new Timestamp(System.currentTimeMillis()));
                            System.out.println(e.getMessage());
                            System.out.println(e.getCode());
                            System.out.println(e.getResponse());
                        } catch (Throwable th) {
                            System.out.println("test");
                            System.out.println(th.getMessage());
                            Log.e("ERROR", "Unable to create session");
                        } finally {
                            zzz[0]++;
                        }
                    }
                }
        );
        return 1;
    }

    public void createSession(String email, String password) throws AppwriteException {
        // Create the session
        int[] q = {0};
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
                        q[0] = getAccount();
                        subscribeToken();
                        while (true) {
                            if (q[0] > 0) {
                                System.out.println("PRE INTENT********************");
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                System.out.println("POST INTENT********************");
                                break;
                            }
                        }
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
                }finally {
                    System.out.println("SESSION HAS BEEN CREATED********************");
                    zzz[0]++;
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
                    } finally {
                        zzz[0]++;
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

    public int getAccount() {
        System.out.println("Starting getAccount()");
        int[] q = {0};
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
                            q[0] = sharedPreferencesHelper.setUser(json);
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
        } finally {
            while (true){
                if (q[0] > 0) break;
            }
            zzz[0]++;
        }
        System.out.println("getAccount() finished");
        return 1;
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

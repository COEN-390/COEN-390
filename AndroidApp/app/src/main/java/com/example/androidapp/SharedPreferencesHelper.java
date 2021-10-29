package com.example.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import kotlin.coroutines.Continuation;
import kotlin.Result;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;
    private Client client;
    private Account account;
    private MutableLiveData<JSONObject> _user;
    private LiveData<JSONObject> user;

    public SharedPreferencesHelper(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.context.getString(R.string.Shared_Preferences), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        this.client = new Client(context)
                .setEndpoint("https://appwrite.orpine.net/v1")
                .setProject("6137a2ef0d4f5");
        this.account = new Account(this.client);
    }

    public String getUsername(){
        return this.sharedPreferences.getString(this.context.getString(R.string.Shared_Preferences_Username), "");
    }

    public void createSession(String email, String password) {
        // Create the session
        try {
            account.createSession(
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
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                    storeUsername();
                                }
                            } catch (Throwable th) {
                                Log.e("ERROR", "Unable to create session");
                            }
                        }
                    }
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
        // Store the username
        this._user = new MutableLiveData<JSONObject>();
        this._user.setValue(null);
        this.user = this._user;
        try {
            storeUsername();
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    public void endSession(){
        try {
            account.deleteSession(
                    "[SESSION_ID]",
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
                                }
                            } catch (Throwable th) {
                                Log.e("ERROR", "Unable to end session");
                            }
                        }
                    }
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
        this.editor.putString(this.context.getString(R.string.Shared_Preferences_Username), "");
    }

    private void storeUsername() throws AppwriteException{
        account.get(
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
                            JSONObject user = new JSONObject(json);
                            _user.postValue(user);
                        }
                    } catch (Throwable th) {
                        Log.e("ERROR", "Unable to get user");
                    }
                }
            }
        );
        if(user.getValue() != null) {
            try {
                this.editor.putString(this.context.getString(R.string.Shared_Preferences_Username), user.getValue().getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to get username");
            }
        }
        else this.editor.putString(this.context.getString(R.string.Shared_Preferences_Username), "");
        this.editor.apply();
    }
}

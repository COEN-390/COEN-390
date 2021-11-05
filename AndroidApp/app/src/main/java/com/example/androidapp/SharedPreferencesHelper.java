package com.example.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

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

    public SharedPreferencesHelper(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.context.getString(R.string.Shared_Preferences), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        // Initialize the Appwrite communication
        this.client = new Client(context)
                .setEndpoint("https://appwrite.orpine.net/v1")
                .setProject("6137a2ef0d4f5");
        this.account = new Account(this.client);
    }

    public void createSession(String email, String password) throws AppwriteException {
        // Create the session
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
                        System.out.println("Creating Session");
                        try {
                            if (o instanceof Result.Failure) {
                                Result.Failure failure = (Result.Failure) o;
                                throw failure.exception;
                            } else {
                                getAccount();
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
    }

    public void endSession(){
        try {
            account.deleteSession(
                    "current",
                    new Continuation<Object>() {
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
                                } else{
                                    editor.putString("user", "{}");
                                    editor.apply();
                                }
                            } catch (AppwriteException e){
                                System.out.println("endSession() " + new Timestamp(System.currentTimeMillis()));
                                System.out.println(e.getMessage());
                                System.out.println(e.getCode());
                                System.out.println(e.getResponse());
                            } catch (Throwable th) {
                                Log.e("ERROR", "Unable to end session");
                            }
                        }
                    }
            );
        } catch (AppwriteException e) {
            System.out.println("endSession() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    private void getAccount() {
        try {
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
                                    editor.putString("user", json);
                                    editor.apply();
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
                    }
            );
        } catch (AppwriteException e){
            System.out.println("storeUsername() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    public String getName(){
        if(!userIsEmpty()){
            try {
                return getUser().getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private JSONObject getUser(){
        String json = sharedPreferences.getString("user", "{}");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userIsEmpty(){
        return getUser().length() == 0 ;
    }
}

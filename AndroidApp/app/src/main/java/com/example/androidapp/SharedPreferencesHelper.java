package com.example.androidapp;

import android.app.Activity;
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
import io.appwrite.models.RealtimeCallback;
import io.appwrite.services.Account;
import io.appwrite.services.Database;
import io.appwrite.services.Realtime;
import io.appwrite.views.CallbackActivity;
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
    private Database events;

    public SharedPreferencesHelper(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.context.getString(R.string.Shared_Preferences), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        // Initialize the Appwrite communication
        this.client = AppwriteController.getClient(context);
        this.account = new Account(this.client);
        this.events = new Database(this.client);

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

    public void setUser(String json) {
        editor.putString("user", json);
        editor.apply();
    }

    private void getEventsList(){
        try {
            events.listDocuments(
                    "61871d8957bbc",
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
                                editor.putString("events", json);
                                editor.apply();
                            }
                        } catch (AppwriteException e) {
                            System.out.println("getEventsList() " + new Timestamp(System.currentTimeMillis()));
                            System.out.println(e.getMessage());
                            System.out.println(e.getCode());
                            System.out.println(e.getResponse());
                        } catch (Throwable th) {
                            Log.e("ERROR", th.toString());
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

    public JSONObject getEvents(){
        getEventsList();
        String json = sharedPreferences.getString("events", "{\"sum\":0,\"documents\":[]}");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client getClient(){
        return this.client;
    }
}

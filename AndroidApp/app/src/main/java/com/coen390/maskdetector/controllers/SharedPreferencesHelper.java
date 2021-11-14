package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coen390.maskdetector.R;
import com.coen390.maskdetector.controllers.AppwriteController;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import io.appwrite.services.Database;
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

    public void getEventsList(){
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

    public JSONArray getEvents(){
        String json = sharedPreferences.getString("events", "{\"sum\":0,\"documents\":[]}");
        try {
            JSONObject events = new JSONObject(json);
            return events.getJSONArray("documents");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public int getEventsSize(){
        String json = sharedPreferences.getString("events", "{\"sum\":0,\"documents\":[]}");
        try {
            JSONObject events = new JSONObject(json);
            return events.getInt("sum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setEvents(JSONArray events){
        // Take the current set of events and modify it accordingly
        String json = sharedPreferences.getString("events", "{\"sum\":0,\"documents\":[]}");
        try {
            JSONObject eventsList = new JSONObject(json);
            eventsList.put("sum", events.length());
            eventsList.put("documents", events);
            editor.putString("events", eventsList.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Client getClient(){
        return this.client;
    }
}

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

    public SharedPreferencesHelper(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.context.getString(R.string.Shared_Preferences), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
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
}

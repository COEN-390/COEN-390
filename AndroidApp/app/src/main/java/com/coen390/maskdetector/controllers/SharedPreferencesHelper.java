package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import com.coen390.maskdetector.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Controller used for data storage through shared preferences
 */
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

    public JSONObject getUser(){
        String json = sharedPreferences.getString("user", "{}");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userIsEmpty(){
        if (getUser() == null || getUser().equals("")){
            return true;
        }
        return getUser().length() == 0 ;
    }

    public int setUser(String json) {
        System.out.println("Setting User as " + json);
        editor.putString("user", json);
        editor.apply();
        System.out.println("User Has Been Set");
        return 1;
    }
}

package com.example.androidapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.context.getString(R.string.Shared_Preferences), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public String getEmail(){
        return this.sharedPreferences.getString(this.context.getString(R.string.Shared_Preferences_Email), "");
    }

    public void setEmail(String username){
        this.editor.putString(this.context.getString(R.string.Shared_Preferences_Email), username);
        this.editor.apply();
    }

    public String getPassword(){
        return this.sharedPreferences.getString(this.context.getString(R.string.Shared_Preferences_Password), null);
    }

    public void setPassword(String password){
        this.editor.putString(this.context.getString(R.string.Shared_Preferences_Password), password);
        this.editor.apply();
    }
}

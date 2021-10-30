package com.example.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActionBar actionBar;
    private MenuInflater menuInflater;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item:
                logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI(){
        welcomeMessage = findViewById(R.id.welcome_message);
        actionBar = getSupportActionBar();
        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());

        actionBar.show();
        actionBar.setHomeButtonEnabled(false);

        sharedPreferencesHelper.getUser().observe(this, jsonObject ->{
            System.out.println(sharedPreferencesHelper.getName());
            if(sharedPreferencesHelper.getName() != ""){
                welcomeMessage.setText("Welcome, " + sharedPreferencesHelper.getName() + "!");
            }
            else{
                goToLoginActivity();
            }
        });
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void logout(){
        sharedPreferencesHelper.endSession();
        sharedPreferencesHelper.clearUser();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
        goToLoginActivity();
    }
}
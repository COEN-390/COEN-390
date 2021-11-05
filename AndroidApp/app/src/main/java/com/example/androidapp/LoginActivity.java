package com.example.androidapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Timestamp;

import io.appwrite.exceptions.AppwriteException;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUI();
    }

    private void setupUI(){
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);

        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());

        actionBar = getSupportActionBar();
        actionBar.hide();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                try {
                    sharedPreferencesHelper.createSession(email, password);
                    // Only login after a certain delay (gives time to hear back from server)
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToMainActivity();
                        }
                    }, 5000);
                } catch (AppwriteException e) {
                    System.out.println("createSession() " + new Timestamp(System.currentTimeMillis()));
                    System.out.println(e.getMessage());
                    System.out.println(e.getCode());
                    System.out.println(e.getResponse());
                }
            }
        });
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
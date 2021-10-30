package com.example.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" ;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActionBar actionBar;
    private MenuInflater menuInflater;
    private TextView welcomeMessage;
    private Button testButton;
    //Notification channel ID. Put it somewhere better
    private String defaultChannel = "defaultChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate Called!");

        Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
        startService(intentBackgroundService);

//        String token = PushNotificationService.getToken(this);
//        Log.d(TAG, "Token Received: " + token);
        tokenCall();

        createNotificationChannel();
        setupUI();
    }

    /**
     * Method used to obtain token for app
     * Taken from: https://stackoverflow.com/a/66696714
     */

    private void tokenCall() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(TAG, "Firebase Cloud Messaging token: "+ token);

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sharedPreferencesHelper.getEmail().equals("")){
            goToLoginActivity();
        }
        else{
            welcomeMessage.setText("Welcome, " + sharedPreferencesHelper.getEmail() + "!");
        }
    }

    //Must do at the start before notifications can happen. Maybe put in main activity onCreate() ?
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //These appear in app settings. Put in notification resource class instead?
            CharSequence name = "SampleSequence";
            String description = "SampleDescription";

            //Set the importance of the notification
            int importance = NotificationManager.IMPORTANCE_HIGH;

            //Actually creating the channel
            NotificationChannel channel = new NotificationChannel(defaultChannel, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
                sharedPreferencesHelper.setEmail("");
                sharedPreferencesHelper.setPassword("");
                Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
                goToLoginActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI(){
        welcomeMessage = findViewById(R.id.welcome_message);
        actionBar = getSupportActionBar();
        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());

        actionBar.show();
        actionBar.setHomeButtonEnabled(false);

        testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification();
            }
        });

    }

    private void notification(){

        //Set the intent you want for when you click on the notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, defaultChannel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Test Notification")
                .setContentText("This is a test :)")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        //Actually display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(420, builder.build()); //Different ID needed for separate cameras? or else can only have one notification for all cameras

    }

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
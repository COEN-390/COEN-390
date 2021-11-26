package com.coen390.maskdetector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;
    private ActionBar actionBar;
    private MenuInflater menuInflater;

    private Button eventLogButton;
    private Button devicesButton;
    private Button usersButton;
    private Button savedEventsButton;

    // Notification channel ID. Put it somewhere better
    private String defaultChannel = "defaultChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate Called!");

        //Loading loading = new Loading(MainActivity.this);

        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        authenticationController = new AuthenticationController(getApplicationContext());

        if (sharedPreferencesHelper.userIsEmpty())
            goToLoginActivity();

        Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
        startService(intentBackgroundService);

        createNotificationChannel();
        setupUI();
    }

    private void setupUI() {
        actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setHomeButtonEnabled(false);

        eventLogButton = findViewById(R.id.eventLogButton);
        devicesButton = findViewById(R.id.devicesButton);
        usersButton = findViewById(R.id.usersButton);
        savedEventsButton = findViewById(R.id.savedEventsButton);

        eventLogButton.setOnClickListener(onClickEventLogButton);
        devicesButton.setOnClickListener(onClickDevicesButton);
        usersButton.setOnClickListener(onClickUsersButton);
        savedEventsButton.setOnClickListener(onClickSavedEventsActivity);
    }

    // Must do at the start before notifications can happen. Maybe put in main
    // activity onCreate() ?
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // These appear in app settings. Put in notification resource class instead?
            CharSequence name = "SampleSequence";
            String description = "SampleDescription";

            // Set the importance of the notification
            int importance = NotificationManager.IMPORTANCE_HIGH;

            // Actually creating the channel
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
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if(/* TODO: user isn't admin */) {
//            menu.findItem(R.id.admin_menu_item).setVisible(false);
//            menu.findItem(R.id.saved_events_menu_item).setVisible(false);
//        }
//        else{
//            menu.findItem(R.id.admin_menu_item).setVisible(true);
//            menu.findItem(R.id.saved_events_menu_item).setVisible(true);
//        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
        case R.id.logout_menu_item:
            logout();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private final Button.OnClickListener onClickEventLogButton = view -> {
        Intent intent = new Intent(this, EventLogActivity.class);
        startActivity(intent);
    };

    private final Button.OnClickListener onClickDevicesButton = view -> {
        Intent intent = new Intent(this, DevicesActivity.class);
        startActivity(intent);
    };

    private final Button.OnClickListener onClickUsersButton = view -> {
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    };

    private final Button.OnClickListener onClickSavedEventsActivity = view -> {
        Intent intent = new Intent(this, SavedEventsActivity.class);
        startActivity(intent);
    };

    private void logout() {
        authenticationController.endSession();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }

}
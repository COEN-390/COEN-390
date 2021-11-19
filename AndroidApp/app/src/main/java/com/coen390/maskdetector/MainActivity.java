package com.coen390.maskdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.coen390.maskdetector.controllers.AppwriteController;
import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.EventsController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.models.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import io.appwrite.services.Realtime;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;
    private ActionBar actionBar;
    private MenuInflater menuInflater;
    private RecyclerView eventsRecyclerView;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsController eventsController;
    // Notification channel ID. Put it somewhere better
    private String defaultChannel = "defaultChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate Called!");

        Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
        startService(intentBackgroundService);

        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        authenticationController = new AuthenticationController(getApplicationContext());
        eventsController = new EventsController(getApplicationContext());
        // if(!sharedPreferencesHelper.userIsEmpty()) {
        // Intent intentBackgroundService = new Intent(this,
        // PushNotificationService.class);
        // startService(intentBackgroundService);
        // }
        // String token = PushNotificationService.getToken(this);
        // Log.d(TAG, "Token Received: " + token);

        createNotificationChannel();
        setupUI();
        setupRecyclerView();
        eventsController.setupEventsRealtime(getApplicationContext(), eventsRecyclerViewAdapter, this);
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
        case R.id.admin_menu_item:
            goToAdminActivity();
            break;
        case R.id.device_menu_item:
            goToDevicesActivity();
            break;
        case R.id.saved_events_menu_item:
            goToSavedEventsActivity();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(false);

        if (sharedPreferencesHelper.userIsEmpty())
            goToLoginActivity();

    }

//    private void notification() {
//
//        // Set the intent you want for when you click on the notification
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, defaultChannel)
//                .setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("Test Notification")
//                .setContentText("This is a test :)").setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        // Actually display the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(420, builder.build()); // Different ID needed for separate cameras? or else can only
//                                                          // have one notification for all cameras
//
//    } // NOT USEFUL ANYMORE

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToAdminActivity() {
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }

    private void goToDevicesActivity() {
        Intent intent = new Intent(this, DevicesActivity.class);
        startActivity(intent);
    }

    private void goToSavedEventsActivity() {
        Intent intent = new Intent(this, SavedEventsActivity.class);
        startActivity(intent);
    }

    protected void goToSavedEventsActivity(String eventId) {
        Intent intent = new Intent(this, SavedEventsActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    private void logout() {
        authenticationController.endSession();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }

    private void setupRecyclerView() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(getApplicationContext(), this);
        eventsController.getEventsList(eventsRecyclerViewAdapter, this, new ArrayList<Event>());

        // Create layout manager and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);
        eventsRecyclerView.setAdapter(eventsRecyclerViewAdapter);
    }
}
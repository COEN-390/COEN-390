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
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.appwrite.services.Realtime;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;
    private ActionBar actionBar;
    private MenuInflater menuInflater;
    private Button testButton;
    private RecyclerView eventsRecyclerView;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private Realtime eventsListener;
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
        setupRealtime();
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(false);

        testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification();
            }
        });

        if (sharedPreferencesHelper.userIsEmpty())
            goToLoginActivity();

    }

    private void notification() {

        // Set the intent you want for when you click on the notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, defaultChannel)
                .setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("Test Notification")
                .setContentText("This is a test :)").setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        // Actually display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(420, builder.build()); // Different ID needed for separate cameras? or else can only
                                                          // have one notification for all cameras

    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToAdminActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    private void goToDevicesActivity() {
        Intent intent = new Intent(this, DevicesActivity.class);
        startActivity(intent);
    }

    private void logout() {
        authenticationController.endSession();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }

    private void setupRecyclerView() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        sharedPreferencesHelper.getEventsList();
        JSONArray events = sharedPreferencesHelper.getEvents();

        // Create layout manager, adapter and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(getApplicationContext(), events);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);
        eventsRecyclerView.setAdapter(eventsRecyclerViewAdapter);
    }

    private void setupRealtime() {
        // Create the connection to the Appwrite server's realtime functionality
        eventsListener = new Realtime(AppwriteController.getClient(getApplicationContext()));
        eventsListener.subscribe(new String[] { "collections.61871d8957bbc.documents" }, (param) -> {
            // Implement the lambda function that will run every time there is a change in
            // the events
            try {
                // Get the values in the payload response
                String eventType = param.getEvent();
                Date timestamp = new Date(param.getTimestamp());
                JSONObject payload = new JSONObject(param.getPayload().toString());
                System.out.println(timestamp.toString() + ": " + eventType);

                // If an event gets created, add it to the saved list of events
                JSONArray events = sharedPreferencesHelper.getEvents();
                if (eventType.equals("database.documents.create")) {
                    JSONObject event = new JSONObject();
                    // Payload is not in the same order, so create a proper new JSON object
                    try {
                        event.put("$id", payload.getString("$id"));
                        event.put("$collection", payload.getString("$collection"));
                        event.put("$permissions", payload.getString("$permissions"));
                        event.put("timestamp", payload.getString("timestamp"));
                        event.put("organizationId", payload.getString("organizationId"));
                        event.put("$deviceId", payload.getString("deviceId"));
                        // Add the new event to the array
                        events.put(event);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Refresh the saved data and the recycler view
                            sharedPreferencesHelper.setEvents(events);
                            eventsRecyclerViewAdapter.updateList();
                            eventsRecyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Event created", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // Otherwise, check which event got modified
                else {
                    for (int i = 0; i < sharedPreferencesHelper.getEventsSize(); i++) {
                        if (payload.getString("$id").equals(events.getJSONObject(i).getString("$id"))) {
                            // If the event got deleted, remove it from the saved list
                            if (eventType.equals("database.documents.delete")) {
                                events.remove(i);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Refresh the saved data and the recycler view
                                        sharedPreferencesHelper.setEvents(events);
                                        eventsRecyclerViewAdapter.updateList();
                                        eventsRecyclerViewAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Event deleted", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                            // If it got updated, modify it
                            else {
                                JSONObject event = new JSONObject();
                                // Payload is not in the same order, so create a proper new JSON object
                                try {
                                    event.put("$id", payload.getString("$id"));
                                    event.put("$collection", payload.getString("$collection"));
                                    event.put("$permissions", payload.getString("$permissions"));
                                    event.put("timestamp", payload.getString("timestamp"));
                                    event.put("organizationId", payload.getString("organizationId"));
                                    event.put("$deviceId", payload.getString("deviceId"));
                                    // Modify the event in the array
                                    events.put(i, event);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Refresh the saved data and the recycler view
                                        sharedPreferencesHelper.setEvents(events);
                                        eventsRecyclerViewAdapter.updateList();
                                        eventsRecyclerViewAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Event updated", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
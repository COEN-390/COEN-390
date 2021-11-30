package com.coen390.maskdetector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.EventsController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.models.Event;

import java.util.ArrayList;

public class EventLogActivity extends AppCompatActivity {

    private static final String TAG = "EventLogActivity";

    private ActionBar actionBar;
    private RecyclerView eventsRecyclerView;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsController eventsController;
    // Notification channel ID. Put it somewhere better
    private String defaultChannel = "defaultChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_log);
        Log.d(TAG, "onCreate Called!");

        Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
        startService(intentBackgroundService);

        eventsController = new EventsController(getApplicationContext());

        setupUI();
        setupRecyclerView();
        eventsController.setupEventsRealtime(getApplicationContext(), eventsRecyclerViewAdapter, this);
    }

    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(true);
    }

    protected void goToSavedEventsActivity(String eventId) {
        Intent intent = new Intent(this, SavedEventsActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(getApplicationContext(), this);
        eventsController.getEventsList(eventsRecyclerViewAdapter, this, new ArrayList<Event>());

        // Create layout manager and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);
        eventsRecyclerView.setAdapter(eventsRecyclerViewAdapter);
    }
}
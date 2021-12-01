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

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class used to setup and implement the EventLogActivity
 */
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
        String receivedLevel = "user";

        Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
        startService(intentBackgroundService);

        //Collecting extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receivedLevel = extras.getString("level");
        }

        eventsController = new EventsController(getApplicationContext());

        //Setting user level on controller
        eventsController.setUserLevel(receivedLevel);

        setupUI();
        setupRecyclerView();
        eventsController.setupEventsRealtime(getApplicationContext(), eventsRecyclerViewAdapter, this);
    }

    /**
     * Method used to setup the UI of the EventLogActivity
     * @throws JSONException
     */
    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * Method used to switch to the SavedEvents Activity
     * @param eventId
     */
    protected void goToSavedEventsActivity(String eventId) {
        Intent intent = new Intent(this, SavedEventsActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    /**
     * Method used to setup the Recycler View containing all logged Events
     */
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
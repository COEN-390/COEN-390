package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coen390.maskdetector.controllers.SavedEventsController;
import com.coen390.maskdetector.models.Event;
import com.coen390.maskdetector.models.SavedEvent;

import java.util.ArrayList;

public class SavedEventsActivity extends AppCompatActivity {

    private SavedEventsController savedEventsController;
    private ActionBar actionBar;
    private RecyclerView savedEventsRecyclerView;
    private SavedEventsRecyclerViewAdapter savedEventsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        savedEventsController = new SavedEventsController(getApplicationContext());

        setupUI();
        setupRecyclerView();
        savedEventsController.setupSavedEventsRealtime(getApplicationContext(), savedEventsRecyclerViewAdapter, this);
    }

    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupRecyclerView(){
        savedEventsRecyclerView = findViewById(R.id.savedEventsRecyclerView);
        savedEventsRecyclerViewAdapter = new SavedEventsRecyclerViewAdapter(getApplicationContext());
        savedEventsController.getSavedEventsList(savedEventsRecyclerViewAdapter, this, new ArrayList<SavedEvent>());

        // Create layout manager and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(savedEventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        savedEventsRecyclerView.setLayoutManager(linearLayoutManager);
        savedEventsRecyclerView.addItemDecoration(dividerItemDecoration);
        savedEventsRecyclerView.setAdapter(savedEventsRecyclerViewAdapter);
    }
}
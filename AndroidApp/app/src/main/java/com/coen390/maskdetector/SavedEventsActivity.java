package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.coen390.maskdetector.controllers.SavedEventsController;
import com.coen390.maskdetector.models.Event;
import com.coen390.maskdetector.models.SavedEvent;

import java.util.ArrayList;

import kotlinx.coroutines.Delay;

public class SavedEventsActivity extends AppCompatActivity {

    private SavedEventsController savedEventsController;
    private ActionBar actionBar;
    private RecyclerView savedEventsRecyclerView;
    private SavedEventsRecyclerViewAdapter savedEventsRecyclerViewAdapter;
    private int highlightedPosition;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        savedEventsController = new SavedEventsController(getApplicationContext());

        setupUI();
        setupRecyclerView();
        savedEventsController.setupSavedEventsRealtime(getApplicationContext(), savedEventsRecyclerViewAdapter, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setupRecyclerView();
    }

    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupRecyclerView(){
        savedEventsRecyclerView = findViewById(R.id.savedEventsRecyclerView);
        extras = getIntent().getExtras();
        if(extras != null){
            String eventId = getIntent().getStringExtra("eventId");
            savedEventsRecyclerViewAdapter = new SavedEventsRecyclerViewAdapter(getApplicationContext(), this, eventId);
        }
        else{
            savedEventsRecyclerViewAdapter = new SavedEventsRecyclerViewAdapter(getApplicationContext(), this, "");
        }
        savedEventsController.getSavedEventsList(savedEventsRecyclerViewAdapter, this, new ArrayList<SavedEvent>());
        if(extras != null){
            savedEventsRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    savedEventsRecyclerView.scrollToPosition(highlightedPosition);
                }

            });
        }

        // Create layout manager and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(savedEventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        savedEventsRecyclerView.setLayoutManager(linearLayoutManager);
        savedEventsRecyclerView.addItemDecoration(dividerItemDecoration);
        savedEventsRecyclerView.setAdapter(savedEventsRecyclerViewAdapter);
    }

    public void setHighlightedPosition(int position){
        this.highlightedPosition = position;
    }
}
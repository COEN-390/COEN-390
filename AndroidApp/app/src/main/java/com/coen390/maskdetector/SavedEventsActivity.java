package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coen390.maskdetector.controllers.SavedEventsController;
import com.coen390.maskdetector.models.SavedEvent;

import java.util.ArrayList;


/**
 * Class used to setup and implement the SavedEventsActivity
 */
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

        String receivedLevel = "admin";

        //Collecting extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receivedLevel = extras.getString("level");
        }

        savedEventsController = new SavedEventsController(getApplicationContext());

        savedEventsController.setUserLevel(receivedLevel);

        setupUI();
        setupRecyclerView();
        savedEventsController.setupSavedEventsRealtime(getApplicationContext(), savedEventsRecyclerViewAdapter, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setupRecyclerView();
    }

    /**
     * Method used to setup the UI of the SavedEventsActivity
     */
    private void setupUI() {
        actionBar = getSupportActionBar();

        actionBar.show();
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * Method used to setup the Recycler View containing all Saved Events
     */
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

    /**
     * Method used to highlight the selected saved event on the recycler view
     * @param position
     */
    public void setHighlightedPosition(int position){
        this.highlightedPosition = position;
    }
}
package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coen390.maskdetector.controllers.DevicesController;

import io.appwrite.exceptions.AppwriteException;

/**
 * Class used to setup and implement the DevicesActivity
 */
public class DevicesActivity extends AppCompatActivity {
    private DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    private RecyclerView devicesRecyclerView;
    private DevicesController devicesController;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        devicesController = new DevicesController(this.getApplicationContext());
        setupUI();
        setupRecyclerView();
    }

    /**
     * Method used to setup the UI of the DevicesActivity
     */
    private void setupUI() {
        actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Method used to setup the Recycler View containing all registered devices
     */
    private void setupRecyclerView() {
        devicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(this.getApplicationContext());
        try {
            devicesController.getDeviceList(devicesRecyclerViewAdapter, this);
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
        // Create layout manager, adapter and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(devicesRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        devicesRecyclerView.setLayoutManager(linearLayoutManager);
        devicesRecyclerView.addItemDecoration(dividerItemDecoration);
        devicesRecyclerView.setAdapter(devicesRecyclerViewAdapter);
    }
}
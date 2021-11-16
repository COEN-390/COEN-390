package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.os.Bundle;
import android.widget.Toast;

import com.coen390.maskdetector.controllers.DevicesController;
import com.coen390.maskdetector.models.Device;

import org.json.JSONArray;

import io.appwrite.exceptions.AppwriteException;

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
    }

    private void setupUI() {
        DevicesActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionBar = getSupportActionBar();
                actionBar.show();
                actionBar.setDisplayHomeAsUpEnabled(true);

                setupRecyclerView();
            }
        });
    }

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
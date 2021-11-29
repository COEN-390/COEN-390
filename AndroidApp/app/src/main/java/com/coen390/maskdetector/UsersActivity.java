package com.coen390.maskdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.controllers.UsersController;
import com.coen390.maskdetector.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView usersRecyclerView;
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    private FloatingActionButton fabCreateUser;
    private UsersController usersController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersController = new UsersController(getApplicationContext());

        setupUI();
        setupRecyclerView();
        usersController.setupUsersRealtime(getApplicationContext(), usersRecyclerViewAdapter, this);
    }

    private void setupUI(){
        actionBar = getSupportActionBar();
        fabCreateUser = findViewById(R.id.fabCreateUser);

        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fabCreateUser.setOnClickListener(view -> new CreateUserDf().show(getSupportFragmentManager(), "CreateUserDf"));
    }

    protected void setupRecyclerView(){
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getApplicationContext(), this);
        usersController.getUsersList(usersRecyclerViewAdapter, this, new ArrayList<User>());

        // Create layout manager, adapter and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(usersRecyclerView.getContext(), linearLayoutManager.getOrientation());

        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.addItemDecoration(dividerItemDecoration);
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
    }
}
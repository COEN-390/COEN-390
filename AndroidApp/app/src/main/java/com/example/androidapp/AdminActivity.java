package com.example.androidapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView usersRecyclerView;
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private FloatingActionButton fabCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setupUI();
        setupRecyclerView();
    }

    private void setupUI(){
        actionBar = getSupportActionBar();
        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        fabCreateUser = findViewById(R.id.fabCreateUser);

        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fabCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateUserDf().show(getSupportFragmentManager(), "CreateUserDf");
            }
        });
    }

    protected void setupRecyclerView(){
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        // TODO: switch demo list for actual users list
        List<String> users = new ArrayList<String>();
        for(int i = 1; i <= 20; i++){
            users.add("Test " + i);
        }

        // Create layout manager, adapter and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getApplicationContext(), users);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(usersRecyclerView.getContext(), linearLayoutManager.getOrientation());

        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.addItemDecoration(dividerItemDecoration);
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
    }
}
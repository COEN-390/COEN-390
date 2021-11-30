package com.coen390.maskdetector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.appwrite.exceptions.AppwriteException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;
    private ActionBar actionBar;
    private MenuInflater menuInflater;

    private Button eventLogButton;
    private Button devicesButton;
    private Button usersButton;
    private Button savedEventsButton;
    private Button testButton;

    private TextView userModeView;
    private TextView userEmailView;

    private Bundle bundle;

    // Notification channel ID. Put it somewhere better
    private String defaultChannel = "defaultChannel";

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate Called!");

        //Loading loading = new Loading(MainActivity.this);

        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        authenticationController = new AuthenticationController(getApplicationContext());

        if (sharedPreferencesHelper.userIsEmpty()){
            System.out.println("BOOTING YOU TO THE MAIN MENU***************************************************");
            goToLoginActivity();
        }else {
            Intent intentBackgroundService = new Intent(this, PushNotificationService.class);
            startService(intentBackgroundService);
            createNotificationChannel();
        }
    }

    @Override
    protected void onStart() {
        try {
            setupUI();
            userView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onStart();
    }

    private void setupUI() throws JSONException {

        JSONObject z = sharedPreferencesHelper.getUser();
        try {
            String y = z.getString("prefs");
            if (y.equals("{}")){
                authenticationController.setUserLevel("user");
            }
        } catch (Exception e){
            System.out.println("fillUser(): JSON Parsing failure");
        } finally {
            actionBar = getSupportActionBar();
            actionBar.show();
            actionBar.setHomeButtonEnabled(false);

            eventLogButton = findViewById(R.id.eventLogButton);
            devicesButton = findViewById(R.id.devicesButton);
            usersButton = findViewById(R.id.usersButton);
            savedEventsButton = findViewById(R.id.savedEventsButton);

            eventLogButton.setOnClickListener(onClickEventLogButton);
            devicesButton.setOnClickListener(onClickDevicesButton);
            usersButton.setOnClickListener(onClickUsersButton);
            savedEventsButton.setOnClickListener(onClickSavedEventsActivity);

            System.out.println("About to set text");
            userModeView = findViewById(R.id.userModeView);
            String x = getUserMode();
            userModeView.setText("Permission Mode: " + x);
            userEmailView = findViewById(R.id.emailVIew);
            String y = getUserEmail();
            userEmailView.setText("User Email: " + y);
            System.out.println("Text set");

        }
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

    /**
     * Method called after SetupUI to determine what buttons the user has access to depending on their level (admin or user)
     * @throws JSONException
     */
    public void userView() throws JSONException {
        String x = getUserMode();

        if(x.equals("admin")) {
            usersButton.setVisibility(View.VISIBLE);
            savedEventsButton.setVisibility(View.VISIBLE);
        }
        else{
            usersButton.setVisibility(View.INVISIBLE);
            savedEventsButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
        case R.id.logout_menu_item:
            logout();
            break;
//        case R.id.delete_menu_item:
//            userDelete();
//            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private final Button.OnClickListener onClickEventLogButton = view -> {
        Intent intent = new Intent(this, EventLogActivity.class);
        startActivity(intent);
    };

    private final Button.OnClickListener onClickDevicesButton = view -> {
        Intent intent = new Intent(this, DevicesActivity.class);
        startActivity(intent);
    };

    private final Button.OnClickListener onClickUsersButton = view -> {
        try {
            if (getUserMode().equals("admin")) {
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: Must be admin.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private final Button.OnClickListener onClickSavedEventsActivity = view -> {
        try {
            if (getUserMode().equals("admin")) {
                Intent intent = new Intent(this, SavedEventsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: Must be admin.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private final Button.OnClickListener onClickTestButton = view -> {
        JSONObject j = sharedPreferencesHelper.getUser();
        String x = j.toString();

    };

    private void logout() {
        authenticationController.endSession();
        sharedPreferencesHelper.setUser("");
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
        finish();
    }

    private void userDelete(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DeleteEventPromptDf deleteEventPromptDf = new DeleteEventPromptDf();
        Bundle newBundle = new Bundle();
        newBundle.putString("message", "Are you sure you want to delete this profile?");
        deleteEventPromptDf.setArguments(newBundle);
        deleteEventPromptDf.show(fragmentManager, "DeleteEventPromptDf");
        // Set up a listener to be able to know if the profile is to be deleted
        FragmentResultListener listener = new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(requestKey.equals("delete")){
                    try {
                        authenticationController.deleteUser(getUserEmail());

                    } catch (AppwriteException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        };
        fragmentManager.setFragmentResultListener("delete", deleteEventPromptDf, listener);
    }

    private String getUserMode() throws JSONException {
        JSONObject j = sharedPreferencesHelper.getUser();
        if (j != null) {
            String k = j.getString("prefs");
            if (k.equals("{}")){System.out.println("ERROR: Prefs are empty?!");return "user";}
            j = new JSONObject(k);
            k = j.getString("nameValuePairs");
            j = new JSONObject(k);
            String x = j.getString("userType");
            return x;
        } else {
            System.out.println("Default Value Returned! This should only show on first user login");
            return "user";
        }
    }

    private String getUserEmail() throws JSONException {
        JSONObject j = sharedPreferencesHelper.getUser();
        String k = j.getString("email");
        return k;
    }

}
package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.coen390.maskdetector.UsersActivity;
import com.coen390.maskdetector.UsersRecyclerViewAdapter;
import com.coen390.maskdetector.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Database;
import io.appwrite.services.Realtime;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

/**
 * Controller used for Devices Monitoring
 */
public class UsersController {
    private Context context;
    private Client client;
    private Database db;

    public UsersController(Context context){
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
    }

    /**
     * Method used to obtain the list of Existing Users
     * @param usersRecyclerViewAdapter
     * @param usersActivity
     * @param users
     */
    public void getUsersList(UsersRecyclerViewAdapter usersRecyclerViewAdapter, UsersActivity usersActivity, List<User> users){
        List<String> filters = new ArrayList<String>();
        try {
            db.listDocuments(
                    "616c952eb6396", // Collection ID
                    filters, // Filters for the search
                    100, // Limit of the number of documents in the payload (cannot go higher than 100)
                    users.size(), // Offset from which to start the search in the DB
                    new Continuation<Object>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object o) {
                            String json = "";
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                    Response response = (Response) o;
                                    json = response.body().string();
                                    JSONObject payload = new JSONObject(json);
                                    JSONArray documentList = payload.getJSONArray("documents");
                                    // Initialize the list of devices from JSON
                                    for (int i = 0; i < documentList.length(); i ++) {
                                        User newUser = new User(documentList.getJSONObject(i));
                                        users.add(newUser);
                                    }
                                    // If not all the users in the DB have been filtered, recursively search again
                                    // ("sum" value is dependant on filters, no need to go through all the documents in the entire server)
                                    if(users.size() < payload.getInt("sum")){
                                        getUsersList(usersRecyclerViewAdapter, usersActivity, users);
                                    }
                                    usersActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            usersRecyclerViewAdapter.setUsersList(users);
                                            usersRecyclerViewAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            } catch (AppwriteException e) {
                                System.out.println("getUsersList() " + new Timestamp(System.currentTimeMillis()));
                                System.out.println(e.getMessage());
                                System.out.println(e.getCode());
                                System.out.println(e.getResponse());
                            } catch (Throwable th) {
                                Log.e("ERROR", th.toString());
                            }
                        }
                    }
            );
        } catch (AppwriteException e){
            System.out.println("storeUsername() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    /**
     * DEPRECATED -- Method used to remove a user from the collection
     * @param email
     */
    public void deleteUserFromCollection(String email){
        List<String> filters = new ArrayList<String>();
        filters.add("email=" + email);
        try {
            db.listDocuments(
                    "616c952eb6396", // Collection ID
                    filters, // Filters for the search
                    new Continuation<Object>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object o) {
                            String json = "";
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                    Response response = (Response) o;
                                    json = response.body().string();
                                    JSONObject payload = new JSONObject(json);
                                    JSONArray documentList = payload.getJSONArray("documents");
                                    ArrayList<User> users = new ArrayList<User>();
                                    // Initialize the list of devices from JSON
                                    for (int i = 0; i < documentList.length(); i ++) {
                                        User newUser = new User(documentList.getJSONObject(i));
                                        users.add(newUser);
                                    }
                                    deleteUser(users.get(0));
                                }
                            } catch (AppwriteException e) {
                                System.out.println("getUsersList() " + new Timestamp(System.currentTimeMillis()));
                                System.out.println(e.getMessage());
                                System.out.println(e.getCode());
                                System.out.println(e.getResponse());
                            } catch (Throwable th) {
                                Log.e("ERROR", th.toString());
                            }
                        }
                    }
            );
        } catch (AppwriteException e){
            System.out.println("storeUsername() " + new Timestamp(System.currentTimeMillis()));
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            System.out.println(e.getResponse());
        }
    }

    /**
     * Method used to setup the recycler view containing the users list
     * @param context
     * @param usersRecyclerViewAdapter
     * @param usersActivity
     */
    public void setupUsersRealtime(Context context, UsersRecyclerViewAdapter usersRecyclerViewAdapter, UsersActivity usersActivity) {
        // Create the connection to the Appwrite server's realtime functionality
        Realtime usersListener = new Realtime(AppwriteController.getClient(context));
        usersListener.subscribe(new String[] { "collections.616c952eb6396.documents" }, User.class, (param) -> {
            // Implement the lambda function that will run every time there is a change in
            // the users
            String eventType = param.getEvent();
            Date timestamp = new Date(param.getTimestamp());
            User user = param.getPayload();

            System.out.println(timestamp.toString() + ": " + eventType);
            // If a user gets created, add it to the saved list of users
            if (eventType.equals("database.documents.create")) {
                // Payload is not in the same order, so create a proper new JSON object
                usersActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usersRecyclerViewAdapter.addUser(user);
                        usersRecyclerViewAdapter.notifyDataSetChanged();
                        Toast.makeText(usersActivity.getApplicationContext(), "User created", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If it got deleted, remove it from the recycler view's list
            else if (eventType.equals("database.documents.delete")) {
                usersActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usersRecyclerViewAdapter.deleteUser(user);
                        usersRecyclerViewAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "User deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If it got updated, modify it in the recycler view's list
            else {
                usersActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usersRecyclerViewAdapter.modifyUser(user);
                        usersRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
            return null;
        });
    }

    /**
     * DEPRECATED -- Method used to delete a user
     * @param user
     */
    public void deleteUser(User user){
        try {
            db.deleteDocument(
                    "616c952eb6396",
                    user.get$id(),
                    new Continuation<Object>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object o) {
                            String json = "";
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                    Response response = (Response) o;
                                    json = response.body().string();
                                }
                            } catch (Throwable th) {
                                Log.e("ERROR", th.toString());
                            }
                        }
                    }
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to create a new user
     * @param name
     * @param email
     * @param userLevel
     */
    public void createUser(String name, String email, String userLevel){
        // Create the map of values
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("email", email);
        values.put("userLevel", userLevel);

        // Create the permissions (write so that the admin can change the event's name)
        List<String> read = new ArrayList<String>();
        List<String> write = new ArrayList<String>();
        read.add("*");
        write.add("*");
        try {
            db.createDocument(
                    "616c952eb6396",
                    values,
                    read,
                    write,
                    new Continuation<Object>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object o) {
                            String json = "";
                            try {
                                if (o instanceof Result.Failure) {
                                    Result.Failure failure = (Result.Failure) o;
                                    throw failure.exception;
                                } else {
                                    Response response = (Response) o;
                                    json = response.body().string();
                                }
                            } catch (Throwable th) {
                                Log.e("ERROR", th.toString());
                            }
                        }
                    }
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }
}

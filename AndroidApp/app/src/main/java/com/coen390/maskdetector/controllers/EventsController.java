package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.coen390.maskdetector.EventsRecyclerViewAdapter;
import com.coen390.maskdetector.OldMainActivity;
import com.coen390.maskdetector.models.Event;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Database;
import io.appwrite.services.Realtime;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

public class EventsController {
    private Context context;
    private Client client;
    private Database db;

    public EventsController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
    }

    public void getEventsList(EventsRecyclerViewAdapter eventsRecyclerViewAdapter, FragmentActivity fragmentActivity, List<Event> events){
        List<String> filters = new ArrayList<String>();
        filters.add("organizationId=testOrganization"); // TODO: check the user's organization
        try {
            db.listDocuments(
                    "61871d8957bbc", // Collection ID
                    filters, // Filters for the search
                    100, // Limit of the number of documents in the payload (cannot go higher than 100)
                    events.size(), // Offset from which to start the search in the DB
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
                                        Event newEvent = new Event(documentList.getJSONObject(i));
                                        events.add(newEvent);
                                    }
                                    // If not all the events in the DB have been filtered, recursively search again
                                    // ("sum" value is dependant on filters, no need to go through all the documents in the entire server)
                                    if(events.size() < payload.getInt("sum")){
                                        getEventsList(eventsRecyclerViewAdapter, fragmentActivity, events);
                                    }
                                    fragmentActivity.runOnUiThread(() -> {
                                        eventsRecyclerViewAdapter.setEventsList(events);
                                        eventsRecyclerViewAdapter.notifyDataSetChanged();
                                    });
                                }
                            } catch (AppwriteException e) {
                                System.out.println("getEventsList() " + new Timestamp(System.currentTimeMillis()));
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


    public void setupEventsRealtime(Context context, EventsRecyclerViewAdapter eventsRecyclerViewAdapter, OldMainActivity oldMainActivity) {
        // Create the connection to the Appwrite server's realtime functionality
        Realtime eventsListener = new Realtime(AppwriteController.getClient(context));
        eventsListener.subscribe(new String[] { "collections.61871d8957bbc.documents" }, (param) -> {
            // Implement the lambda function that will run every time there is a change in
            // the events
            try {
                // Get the values in the payload response
                String eventType = param.getEvent();
                Date timestamp = new Date(param.getTimestamp());
                JSONObject payload = new JSONObject(param.getPayload().toString());
                Event event = new Event(payload);
                // Check if the modification is not for the user's organization, quit the realtime update
                if(!payload.getString("organizationId").equals("testOrganization")) return null; // TODO: check the user's organization

                System.out.println(timestamp.toString() + ": " + eventType);
                // If an event gets created, add it to the saved list of events
                if (eventType.equals("database.documents.create")) {
                    // Payload is not in the same order, so create a proper new JSON object
                    oldMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventsRecyclerViewAdapter.addEvent(event);
                            eventsRecyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(oldMainActivity.getApplicationContext(), "New Alert!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // If it got deleted, remove it from the recycler view's list
                else if (eventType.equals("database.documents.delete")) {
                    oldMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventsRecyclerViewAdapter.deleteEvent(event);
                            eventsRecyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(context, "Alert deleted", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // If it got updated, modify it in the recycler view's list
                else {
                    oldMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventsRecyclerViewAdapter.modifyEvent(event);
                            eventsRecyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(oldMainActivity.getApplicationContext(), "Alert modified", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}

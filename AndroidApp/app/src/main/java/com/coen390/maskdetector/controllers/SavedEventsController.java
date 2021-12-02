package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.coen390.maskdetector.SavedEventsActivity;
import com.coen390.maskdetector.SavedEventsRecyclerViewAdapter;
import com.coen390.maskdetector.models.Event;
import com.coen390.maskdetector.models.SavedEvent;

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

public class SavedEventsController {
    private Context context;
    private Client client;
    private Database db;
    private EventsController eventsController;
    static private String userLevel;

    public SavedEventsController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
        this.eventsController = new EventsController(context);
    }

    public void getSavedEventsList(SavedEventsRecyclerViewAdapter savedEventsRecyclerViewAdapter, SavedEventsActivity savedEventsActivity, List<SavedEvent> events){
        List<String> filters = new ArrayList<String>();
        filters.add("organizationId=testOrganization"); // TODO: check the user's organization
        try {
            db.listDocuments(
                    "61968895f33a0", // Collection ID
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
                                        SavedEvent newEvent = new SavedEvent(documentList.getJSONObject(i));
                                        events.add(newEvent);
                                    }
                                    // If not all the events in the DB have been filtered, recursively search again
                                    // ("sum" value is dependant on filters, no need to go through all the documents in the entire server)
                                    if(events.size() < payload.getInt("sum")){
                                        getSavedEventsList(savedEventsRecyclerViewAdapter, savedEventsActivity, events);
                                    }
                                    savedEventsActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            savedEventsRecyclerViewAdapter.setEventsList(events);
                                            savedEventsRecyclerViewAdapter.notifyDataSetChanged();
                                        }
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

    public void setupSavedEventsRealtime(Context context, SavedEventsRecyclerViewAdapter savedEventsRecyclerViewAdapter, SavedEventsActivity savedEventsActivity) {
        // Create the connection to the Appwrite server's realtime functionality
        Realtime savedEventsListener = new Realtime(AppwriteController.getClient(context));
        savedEventsListener.subscribe(new String[] { "collections.61968895f33a0.documents" }, SavedEvent.class, (param) -> {
            // Implement the lambda function that will run every time there is a change in
            // the events
            String eventType = param.getEvent();
            Date timestamp = new Date(param.getTimestamp());
            SavedEvent savedEvent = param.getPayload();
            // Check if the modification is not for the user's organization, quit the realtime update
            if(!savedEvent.getOrganizationId().equals("testOrganization")) return null; // TODO: check the user's organization

            System.out.println(timestamp.toString() + ": " + eventType);
            // If an event gets created, add it to the saved list of events
            if (eventType.equals("database.documents.create")) {
                // Payload is not in the same order, so create a proper new JSON object
                savedEventsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        savedEventsRecyclerViewAdapter.addEvent(savedEvent);
                        savedEventsRecyclerViewAdapter.notifyDataSetChanged();
                        Toast.makeText(savedEventsActivity.getApplicationContext(), "Saved event created", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If it got deleted, remove it from the recycler view's list
            else if (eventType.equals("database.documents.delete")) {
                savedEventsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        savedEventsRecyclerViewAdapter.deleteEvent(savedEvent);
                        savedEventsRecyclerViewAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "Saved event deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If it got updated, modify it in the recycler view's list
            else {
                savedEventsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        savedEventsRecyclerViewAdapter.modifyEvent(savedEvent);
                        savedEventsRecyclerViewAdapter.notifyDataSetChanged();
                        //Toast.makeText(savedEventsActivity.getApplicationContext(), "Alert modified", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        });
    }

    public void createSavedEvent(String name, Event event){
        // Create the map of values
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("timestamp", event.getTimestamp());
        values.put("organizationId", event.getOrganizationId());
        values.put("deviceId", event.getDeviceId());
        values.put("eventId", event.get$id());
        values.put("fileId", event.getFileId());

        // Create the permissions (write so that the admin can change the event's name)
        List<String> read = new ArrayList<String>();
        List<String> write = new ArrayList<String>();
        read.add("*");
        write.add("*");
        try {
            db.createDocument(
                    "61968895f33a0",
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

    public void updateSavedEvent(SavedEvent savedEvent) {
        // Create the map of values
        Map<String, Object> values = new HashMap<>();
        values.put("name", savedEvent.getName());
        values.put("timestamp", savedEvent.getTimestamp());
        values.put("organizationId", savedEvent.getOrganizationId());
        values.put("deviceId", savedEvent.getDeviceId());
        values.put("eventId", savedEvent.getEventId());

        try {
            db.updateDocument(
                    "61968895f33a0",
                    savedEvent.get$id(),
                    values,
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

    public void deleteSavedEvent(SavedEvent savedEvent){
        try {
            db.deleteDocument(
                    "61968895f33a0",
                    savedEvent.get$id(),
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
                                    Event event = new Event(new JSONObject(savedEvent.toString()));
                                    event.setSaved(false);
                                    eventsController.updateEvent(event);
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

    public void setUserLevel(String l){
        SavedEventsController.userLevel = l;
    }

    public String getUserLevel(){
        return  SavedEventsController.userLevel;
    }

}

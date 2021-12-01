package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.coen390.maskdetector.EventLogActivity;
import com.coen390.maskdetector.EventsRecyclerViewAdapter;
import com.coen390.maskdetector.models.Event;

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

public class EventsController {
    private Context context;
    private Client client;
    private Database db;
    static private String userLevel;

    public EventsController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
    }

    public void getEventsList(EventsRecyclerViewAdapter eventsRecyclerViewAdapter, EventLogActivity eventLogActivity, List<Event> events){
        List<String> filters = new ArrayList<String>();
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
                                        getEventsList(eventsRecyclerViewAdapter, eventLogActivity, events);
                                    }
                                    eventLogActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            eventsRecyclerViewAdapter.setEventsList(events);
                                            eventsRecyclerViewAdapter.notifyDataSetChanged();
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

    public void setupEventsRealtime(Context context, EventsRecyclerViewAdapter eventsRecyclerViewAdapter, EventLogActivity eventLogActivity) {
        // Create the connection to the Appwrite server's realtime functionality
        Realtime eventsListener = new Realtime(AppwriteController.getClient(context));
        eventsListener.subscribe(new String[] { "collections.61871d8957bbc.documents" }, Event.class, (param) -> {
            // Implement the lambda function that will run every time there is a change in
            // the events
            String eventType = param.getEvent();
            Date timestamp = new Date(param.getTimestamp());
            Event event = param.getPayload();
            // Check if the modification is not for the user's organization, quit the realtime update
            if(!event.getOrganizationId().equals("testOrganization")) return null; // TODO: check the user's organization

            System.out.println(timestamp.toString() + ": " + eventType);
            // If an event gets created, add it to the saved list of events
            if (eventType.equals("database.documents.create")) {
                // Payload is not in the same order, so create a proper new JSON object
                eventLogActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventsRecyclerViewAdapter.addEvent(event);
                        eventsRecyclerViewAdapter.notifyDataSetChanged();
                        Toast.makeText(eventLogActivity.getApplicationContext(), "New Alert!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If it got deleted, remove it from the recycler view's list
            else if (eventType.equals("database.documents.delete")) {
                eventLogActivity.runOnUiThread(new Runnable() {
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
                eventLogActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventsRecyclerViewAdapter.modifyEvent(event);
                        eventsRecyclerViewAdapter.notifyDataSetChanged();
                        //Toast.makeText(eventLogActivity.getApplicationContext(), "Alert modified", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        });
    }

    public void updateEvent(Event event){
        // Create the map of values
        Map<String, Object> values = new HashMap<>();
        values.put("timestamp", event.getTimestamp());
        values.put("organizationId", event.getOrganizationId());
        values.put("deviceId", event.getDeviceId());
        values.put("saved", event.isSaved());

        try {
            db.updateDocument(
                    "61871d8957bbc",
                    event.get$id(),
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

    public void deleteEvent(Event event){
        try {
            db.deleteDocument(
                    "61871d8957bbc",
                    event.get$id(),
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

    public void setUserLevel(String l){
        EventsController.userLevel = l;
    }

    public String getUserLevel(){
        return  EventsController.userLevel;
    }
}

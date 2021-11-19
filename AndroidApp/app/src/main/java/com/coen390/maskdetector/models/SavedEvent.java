package com.coen390.maskdetector.models;

import org.json.JSONException;
import org.json.JSONObject;

public class SavedEvent {

    private String name;
    private Event event;
    private String eventId; // The original event's ID

    public SavedEvent(JSONObject event){
        try {
            this.name = event.getString("name");
            this.event = new Event(event);
            this.eventId = this.event.get$id();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Event getEvent() {
        return event;
    }

    public String getEventId() {
        return eventId;
    }

    public void setName(String name){
        this.name = name;
    }
}

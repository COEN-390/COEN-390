package com.coen390.maskdetector.models;

import org.json.JSONException;
import org.json.JSONObject;

public class SavedEvent {

    public String name;
    public Event event;

    public SavedEvent(JSONObject event){
        try {
            this.name = event.getString("name");
            this.event = new Event(event);
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

    public void setName(String name){
        this.name = name;
    }
}

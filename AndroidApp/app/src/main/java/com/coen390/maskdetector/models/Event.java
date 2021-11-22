package com.coen390.maskdetector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Event {
    private String $id;
    private String $collection;
    private Permissions $permissions;
    private Double timestamp;
    private String organizationId;
    private String deviceId;
    private boolean saved;

    public Event(JSONObject event){
        try {
            this.$id = event.getString("$id");
            this.$collection = event.getString("$collection");
            this.$permissions = new Permissions(event.getJSONObject("$permissions"));
            this.timestamp = event.getDouble("timestamp");
            this.organizationId = event.getString("organizationId");
            this.deviceId = event.getString("deviceId");
            this.saved = event.getBoolean("saved");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // When creating an event from within the SavedEvent constructor
        try {
            this.$id = event.getString("eventId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Permissions{
        private ArrayList<String> read;
        private ArrayList<String> write;

        public Permissions(JSONObject permissions){
            try {
                JSONArray readPermissions = permissions.getJSONArray("read");
                this.read = new ArrayList<String>();
                for(int i = 0; i < readPermissions.length(); i++){
                    this.read.add(readPermissions.getString(i));
                }
                JSONArray writePermissions = permissions.getJSONArray("write");
                this.write = new ArrayList<String>();
                for(int i = 0; i < writePermissions.length(); i++){
                    this.write.add(writePermissions.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Getter methods

        public ArrayList<String> getRead() {
            return read;
        }

        public ArrayList<String> getWrite() {
            return write;
        }

        // Setter methods

        public void setRead(ArrayList<String> read) {
            this.read = read;
        }

        public void setWrite(ArrayList<String> write) {
            this.write = write;
        }
    }

    public String toString() {
        String json = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Getter Methods

    public String get$id() {
        return $id;
    }

    public String get$collection() {
        return $collection;
    }

    public Permissions get$permissions() {
        return $permissions;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public boolean isSaved() {
        return saved;
    }

    // Setter Methods

    public void set$id(String id) {
        this.$id = id;
    }

    public void set$collection(String collection) {
        this.$collection = collection;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}

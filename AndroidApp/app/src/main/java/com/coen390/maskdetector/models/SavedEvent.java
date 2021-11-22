package com.coen390.maskdetector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SavedEvent {

    private String $id;
    private String $collection;
    private Permissions $permissions;
    private String name;
    private Double timestamp;
    private String organizationId;
    private String deviceId;
    private String eventId;

    public SavedEvent(JSONObject event){
        try {
            this.$id = event.getString("$id");
            this.$collection = event.getString("$collection");
            this.$permissions = new Permissions(event.getJSONObject("$permissions"));
            this.name = event.getString("name");
            this.timestamp = event.getDouble("timestamp");
            this.organizationId = event.getString("organizationId");
            this.deviceId = event.getString("deviceId");
            this.eventId = event.getString("eventId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Permissions {
        private ArrayList<String> read;
        private ArrayList<String> write;

        public Permissions(JSONObject permissions) {
            try {
                JSONArray readPermissions = permissions.getJSONArray("read");
                this.read = new ArrayList<String>();
                for (int i = 0; i < readPermissions.length(); i++) {
                    this.read.add(readPermissions.getString(i));
                }
                JSONArray writePermissions = permissions.getJSONArray("write");
                this.write = new ArrayList<String>();
                for (int i = 0; i < writePermissions.length(); i++) {
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

    public String getName() {
        return name;
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

    public String getEventId() {
        return eventId;
    }

    public void setName(String name){
        this.name = name;
    }
}

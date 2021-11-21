package com.coen390.maskdetector.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String id;
    private String collection;
    private ArrayList<String> readPermissions;
    private ArrayList<String> writePermissions;
    private Date timestamp;
    private String organizationId;
    private String deviceId;

    public Event(JSONObject event){
        try {
            this.id = event.getString("$id");
            this.collection = event.getString("$collection");
            JSONArray ReadPermissions = event.getJSONObject("$permissions").getJSONArray("read");
            this.readPermissions = new ArrayList<String>();
            for(int i = 0; i < ReadPermissions.length(); i++){
                this.readPermissions.add(ReadPermissions.getString(i));
            }
            JSONArray WritePermissions = event.getJSONObject("$permissions").getJSONArray("write");
            this.writePermissions = new ArrayList<String>();
            for(int i = 0; i < WritePermissions.length(); i++){
                this.writePermissions.add(WritePermissions.getString(i));
            }
            this.timestamp = new Date(((long) event.getDouble("timestamp")) * 1000);
            this.organizationId = event.getString("organizationId");
            this.deviceId = event.getString("deviceId");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Getter Methods

    public String getId() {
        return id;
    }

    public String getCollection() {
        return collection;
    }

    public ArrayList<String> getReadPermissions() {
        return readPermissions;
    }
    public ArrayList<String> getWritePermissions() {
        return writePermissions;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setReadPermissions(ArrayList<String> readPermissions) {
        this.readPermissions = readPermissions;
    }

    public void setWritePermissions(ArrayList<String> writePermissions) {
        this.writePermissions = writePermissions;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

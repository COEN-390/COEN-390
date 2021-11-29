package com.coen390.maskdetector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private String $id;
    private String $collection;
    private User.Permissions $permissions;
    private String email;
    private String organizationId;
    private String name;
    private String userLevel;

    public User(JSONObject user){
        try {
            this.$id = user.getString("$id");
            this.$collection = user.getString("$collection");
            this.$permissions = new User.Permissions(user.getJSONObject("$permissions"));
            this.email = user.getString("email");
            this.organizationId = user.getString("organizationId");
            this.name = user.getString("name");
            this.userLevel = user.getString("userLevel");
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

    public User.Permissions get$permissions() {
        return $permissions;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public String get$collection() {
        return $collection;
    }

    // Setter Methods

    public void set$id(String id) {
        this.$id = id;
    }

    public void set$collection(String collection) {
        this.$collection = collection;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void set$permissions(Permissions $permissions) {
        this.$permissions = $permissions;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
}

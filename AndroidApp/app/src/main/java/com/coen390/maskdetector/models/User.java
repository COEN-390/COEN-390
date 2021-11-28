package com.coen390.maskdetector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {
    private String $id;
    private String name;
    private float registration;
    private float status;
    private float passwordUpdate;
    private String email;
    private boolean emailVerification;
    Prefs PrefsObject;

    private class Prefs {
        NameValuePairs NameValuePairsObject;

        private class NameValuePairs {
            private String userType;
            private String organizationId;

            // Getter Methods

            public String getUserType() {
                return userType;
            }

            public String getOrganizationId(){
                return organizationId;
            }

            // Setter Methods

            public void setUserType(String userType) {
                this.userType = userType;
            }

            public void setOrganizationId(String organizationId) {
                this.organizationId = organizationId;
            }
        }

        // Getter Methods

        public NameValuePairs getNameValuePairs() {
            return NameValuePairsObject;
        }

        // Setter Methods

        public void setNameValuePairs(NameValuePairs nameValuePairsObject) {
            this.NameValuePairsObject = nameValuePairsObject;
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

    public String getName() {
        return name;
    }

    public float getRegistration() {
        return registration;
    }

    public float getStatus() {
        return status;
    }

    public float getPasswordUpdate() {
        return passwordUpdate;
    }

    public String getEmail() {
        return email;
    }

    public boolean getEmailVerification() {
        return emailVerification;
    }

    public Prefs getPrefs() {
        return PrefsObject;
    }

    // Setter Methods

    public void set$id(String $id) {
        this.$id = $id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistration(float registration) {
        this.registration = registration;
    }

    public void setStatus(float status) {
        this.status = status;
    }

    public void setPasswordUpdate(float passwordUpdate) {
        this.passwordUpdate = passwordUpdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerification(boolean emailVerification) {
        this.emailVerification = emailVerification;
    }

    public void setPrefs(Prefs prefsObject) {
        this.PrefsObject = prefsObject;
    }
}

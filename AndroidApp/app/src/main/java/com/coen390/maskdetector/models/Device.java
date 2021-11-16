package com.coen390.maskdetector.models;

public class Device {
    private String deviceId;
    private String organizationId;
    private Double healthCheckTimestamp;

    public Device(String deviceId, String organizationId, Double healthCheckTimestamp) {
        this.deviceId = deviceId;
        this.organizationId = organizationId;
        this.healthCheckTimestamp = healthCheckTimestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Double getHealthCheckTimestamp() {
        return healthCheckTimestamp;
    }

    public void setHealthCheckTimestamp(Double healthCheckTimestamp) {
        this.healthCheckTimestamp = healthCheckTimestamp;
    }
}

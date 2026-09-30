package com.example.fleet_monitor.model;

import java.time.Instant;

public class Device {

    private final String id;
    private final String name;

    private Instant lastHeartbeat;
    private String lastHeartbeatStatus;

    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getLastHeartbeatStatus() {
        return lastHeartbeatStatus;
    }

    public void setLastHeartbeatStatus(String lastHeartbeatStatus) {
        this.lastHeartbeatStatus = lastHeartbeatStatus;
    }
}

// com/smartcity/model/TrafficSignal.java
package com.smartcity.model;

import java.io.Serializable;

public class TrafficSignal implements Serializable {
    private String signalId;
    private String location;
    private String state;
    private long lastUpdated;

    public TrafficSignal(String signalId, String location, String state) {
        this.signalId = signalId;
        this.location = location;
        this.state = state;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getSignalId() { return signalId; }
    public String getLocation() { return location; }
    public String getState() { return state; }
    public void setState(String state) {
        this.state = state;
        this.lastUpdated = System.currentTimeMillis();
    }
    public long getLastUpdated() { return lastUpdated; }
}
// com/smartcity/model/Accident.java
package com.smartcity.model;

import java.io.Serializable;
import java.util.Date;

public class Accident implements Serializable {
    private String location;
    private long timestamp;
    private String status;

    public Accident(String location, long timestamp) {
        this.location = location;
        this.timestamp = timestamp;
        this.status = "REPORTED";
    }

    public String getLocation() { return location; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toString() {
        return new Date(timestamp) + " - Accident at " + location + " - Status: " + status;
    }
}
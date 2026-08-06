// com/smartcity/model/EmergencyVehicle.java
package com.smartcity.model;

public class EmergencyVehicle {
    private String vehicleId;
    private String type; // AMBULANCE, FIRE_TRUCK, POLICE
    private String location;
    private boolean available;

    public EmergencyVehicle(String vehicleId, String type, String location) {
        this.vehicleId = vehicleId;
        this.type = type;
        this.location = location;
        this.available = true;
    }

    public String getVehicleId() { return vehicleId; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setLocation(String location) { this.location = location; }
}
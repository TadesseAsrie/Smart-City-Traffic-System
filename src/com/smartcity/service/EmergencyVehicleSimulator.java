package com.smartcity.service;

import com.smartcity.rmi.ControlCenterRemote;
import java.rmi.RemoteException;

public class EmergencyVehicleSimulator implements Runnable {
    private String vehicleId;
    private String vehicleType;
    private String[] routeSignals; // Signals along the route
    private ControlCenterRemote controlCenter;
    private boolean active;

    public EmergencyVehicleSimulator(String vehicleId, String vehicleType, String[] routeSignals, ControlCenterRemote controlCenter) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.routeSignals = routeSignals;
        this.controlCenter = controlCenter;
        this.active = true;
    }

    @Override
    public void run() {
        System.out.printf("[%s %s] DEPLOYED. Route: %s\n", vehicleType, vehicleId, String.join(" -> ", routeSignals));

        for (String signalId : routeSignals) {
            if (!active) break;
            try {
                // Request priority at each signal
                System.out.printf("[%s %s] Approaching signal %s. Requesting priority...\n", vehicleType, vehicleId, signalId);
                String response = controlCenter.requestEmergencyPriority(vehicleId, signalId, "EMERGENCY");
                System.out.printf("[%s %s] %s\n", vehicleType, vehicleId, response);

                // Simulate travel time to next intersection
                Thread.sleep(5000); // 5 seconds per signal in simulation

            } catch (RemoteException | InterruptedException e) {
                System.err.println("Error during priority request: " + e.getMessage());
            }
        }

        System.out.printf("[%s %s] Reached destination. Priority no longer needed.\n", vehicleType, vehicleId);
    }

    public void stop() {
        active = false;
    }
}
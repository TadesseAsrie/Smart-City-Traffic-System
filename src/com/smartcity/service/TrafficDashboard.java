// com/smartcity/service/TrafficDashboard.java
package com.smartcity.service;

import com.smartcity.rmi.ControlCenterRemote;
import com.smartcity.rpc.TrafficService;
import java.rmi.RemoteException;

public class TrafficDashboard implements Runnable {
    private ControlCenterRemote controlCenter;
    private TrafficService trafficService;
    private boolean running;

    public TrafficDashboard(ControlCenterRemote controlCenter, TrafficService trafficService) {
        this.controlCenter = controlCenter;
        this.trafficService = trafficService;
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                displayDashboard();
                Thread.sleep(3600000); // Update every 10 seconds
            } catch (InterruptedException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayDashboard() throws RemoteException {
        System.out.println("\n" + "=".repeat(600));
        System.out.println("       SMART CITY TRAFFIC MANAGEMENT DASHBOARD");
        System.out.println("=".repeat(600));

        System.out.println("\n[RMI] Traffic Sensors Status:");
        String status = controlCenter.viewTrafficStatus();
        System.out.println(status);

        System.out.println("\n[RPC] Traffic Signals Status:");
        var signals = trafficService.getAllSignals();
        for (var signal : signals.values()) {
            System.out.printf("Signal %s at %s: %s\n",
                    signal.getSignalId(), signal.getLocation(), signal.getState());
        }

        System.out.println("=".repeat(600));
        System.out.println("Dashboard updated at: " + new java.util.Date());
        System.out.println("=".repeat(600));
    }

    public void stop() {
        running = false;
    }
}
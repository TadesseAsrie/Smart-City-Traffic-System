package com.smartcity.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class ControlCenterImpl extends UnicastRemoteObject implements ControlCenterRemote {
    private ConcurrentHashMap<String, TrafficSensorRemote> sensors = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> alerts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> activePriorities = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> signalStates = new ConcurrentHashMap<>();

    public ControlCenterImpl() throws RemoteException {
        super();
        signalStates.put("SIG_001", "GREEN");
        signalStates.put("SIG_002", "RED");
        signalStates.put("SIG_003", "YELLOW");
    }

    @Override
    public void receiveAlert(String sensorId, int congestionLevel, String location) throws RemoteException {
        String alert = String.format("ALERT: Sensor %s at %s - Congestion: %d%%",
                sensorId, location, congestionLevel);
        alerts.put(sensorId + "_" + System.currentTimeMillis(), alert);
        System.out.println("[ControlCenter] " + alert);

        if (congestionLevel > 80) {
            handleCongestion(sensorId, congestionLevel);
        } else if (congestionLevel > 50) {
            System.out.println("[ControlCenter] RECOMMENDATION: Increase green light duration");
        } else {
            System.out.println("[ControlCenter] RECOMMENDATION: Normal traffic flow");
        }
    }

    @Override
    public void registerSensor(TrafficSensorRemote sensor) throws RemoteException {
        sensors.put(sensor.getSensorId(), sensor);
        System.out.println("[ControlCenter] Sensor registered: " + sensor.getSensorId() + " at " + sensor.getLocation());
    }

    @Override
    public String viewTrafficStatus() throws RemoteException {
        StringBuilder sb = new StringBuilder("\n=== TRAFFIC STATUS ===\n");
        sb.append("Traffic Signals:\n");
        signalStates.forEach((sig, state) -> sb.append("  ").append(sig).append(": ").append(state).append("\n"));
        sb.append("\nActive Priorities:\n");
        if (activePriorities.isEmpty()) {
            sb.append("  None\n");
        } else {
            activePriorities.forEach((sig, vehicle) -> sb.append("  ").append(sig).append(" -> ").append(vehicle).append("\n"));
        }
        sb.append("\nSensor Congestion:\n");
        for (TrafficSensorRemote sensor : sensors.values()) {
            try {
                sb.append(String.format("  Sensor %s (%s): %d%%\n",
                        sensor.getSensorId(), sensor.getLocation(), sensor.detectTraffic()));
            } catch (RemoteException e) {
                sb.append("  Error reading sensor\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String getSensorReport(String sensorId) throws RemoteException {
        TrafficSensorRemote sensor = sensors.get(sensorId);
        if (sensor == null) {
            return "Error: Sensor ID '" + sensorId + "' not found. Available: " + sensors.keySet();
        }
        int congestion = sensor.detectTraffic();
        String location = sensor.getLocation();
        return String.format(
                "\n========== SENSOR REPORT ==========\n" +
                        "Sensor ID: %s\nLocation: %s\nCongestion: %d%%\nStatus: %s\n===================================\n",
                sensorId, location, congestion,
                congestion > 80 ? "SEVERE - PRIORITY MODE ACTIVATED" : (congestion > 50 ? "MODERATE" : "LIGHT")
        );
    }

    @Override
    public String requestEmergencyPriority(String emergencyVehicleId, String signalId, String direction) throws RemoteException {
        if (!signalStates.containsKey(signalId)) {
            return "Error: Signal " + signalId + " not found.";
        }
        if (activePriorities.containsKey(signalId)) {
            String currentVehicle = activePriorities.get(signalId);
            if (!currentVehicle.equals(emergencyVehicleId)) {
                return "Priority already granted to " + currentVehicle + ". Please wait.";
            }
        }
        String oldState = signalStates.get(signalId);
        signalStates.put(signalId, "PRIORITY_GREEN");
        activePriorities.put(signalId, emergencyVehicleId);
        String message = String.format(
                "[PRIORITY] Emergency vehicle %s granted priority at signal %s (direction %s). Signal changed from %s to PRIORITY_GREEN.",
                emergencyVehicleId, signalId, direction, oldState
        );
        System.out.println("[ControlCenter] " + message);

        // Auto-release after 15 seconds
        new Thread(() -> {
            try {
                Thread.sleep(15000);
                releaseEmergencyPriority(signalId);
            } catch (InterruptedException | RemoteException e) {}
        }).start();
        return message;
    }

    @Override
    public void releaseEmergencyPriority(String signalId) throws RemoteException {
        if (activePriorities.containsKey(signalId)) {
            String vehicle = activePriorities.remove(signalId);
            signalStates.put(signalId, "GREEN");
            System.out.printf("[ControlCenter] Priority released for signal %s (vehicle %s). Signal back to GREEN.\n", signalId, vehicle);
        }
    }

    @Override
    public void handleCongestion(String sensorId, int congestionLevel) throws RemoteException {
        System.out.printf("[ControlCenter] SEVERE CONGESTION (%d%%) detected at %s! Activating emergency protocols.\n",
                congestionLevel, sensorId);
        String nearbySignal = "SIG_001";
        signalStates.put(nearbySignal, "GREEN_EXTENDED");
        System.out.printf("[ControlCenter] Extended green light at signal %s to clear congestion.\n", nearbySignal);

        new Thread(() -> {
            try {
                Thread.sleep(30000);
                if (!activePriorities.containsKey(nearbySignal)) {
                    signalStates.put(nearbySignal, "GREEN");
                    System.out.printf("[ControlCenter] Signal %s restored to normal GREEN.\n", nearbySignal);
                }
            } catch (InterruptedException e) {}
        }).start();
    }
}
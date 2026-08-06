package com.smartcity.service;

import com.smartcity.model.Accident;
import com.smartcity.model.EmergencyVehicle;
import com.smartcity.rmi.ControlCenterRemote;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EmergencyDispatcher handles emergency vehicle dispatch and coordinates with
 * the RMI Control Center to request traffic signal priority.
 */
public class EmergencyDispatcher {
    private ConcurrentHashMap<String, EmergencyVehicle> vehicles = new ConcurrentHashMap<>();
    private Random random = new Random();
    private ControlCenterRemote controlCenter; // Will be set later

    public EmergencyDispatcher() {
        // Initialize emergency vehicles
        vehicles.put("AMB_001", new EmergencyVehicle("AMB_001", "AMBULANCE", "40.7128,-74.0060"));
        vehicles.put("AMB_002", new EmergencyVehicle("AMB_002", "AMBULANCE", "40.7148,-74.0080"));
        vehicles.put("FIRE_001", new EmergencyVehicle("FIRE_001", "FIRE_TRUCK", "40.7138,-74.0070"));
        vehicles.put("POL_001", new EmergencyVehicle("POL_001", "POLICE", "40.7158,-74.0090"));
    }

    /**
     * Set the RMI control center reference (called from Main after RMI is ready)
     */
    public void setControlCenter(ControlCenterRemote center) {
        this.controlCenter = center;
    }

    /**
     * Original dispatch method (used when no priority is needed)
     */
    public String dispatchEmergencyVehicle(Accident accident) {
        // Find first available vehicle
        Optional<EmergencyVehicle> available = vehicles.values().stream()
                .filter(EmergencyVehicle::isAvailable)
                .findFirst();

        if (available.isPresent()) {
            EmergencyVehicle vehicle = available.get();
            vehicle.setAvailable(false);
            int eta = random.nextInt(10) + 1; // 1-10 minutes
            String dispatchMsg = String.format("Dispatched %s %s to accident at %s. ETA: %d minutes",
                    vehicle.getType(), vehicle.getVehicleId(), accident.getLocation(), eta);

            // Schedule vehicle to become available again after ETA
            new Thread(() -> {
                try {
                    Thread.sleep(eta * 60 * 1000L);
                    vehicle.setAvailable(true);
                    System.out.println("[Dispatcher] " + vehicle.getType() + " " + vehicle.getVehicleId() + " is now available");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            return dispatchMsg;
        } else {
            return "No emergency vehicles available. Accident queued.";
        }
    }

    /**
     * New dispatch method that uses traffic signal priority via RMI.
     * This method should be called when RMI is available and we want
     * the emergency vehicle to have green lights along its route.
     */
    public String dispatchEmergencyVehicleWithPriority(Accident accident, String[] routeSignals) {
        if (controlCenter == null) {
            return "Error: Control center not set. Cannot request priority.";
        }

        // Find first available vehicle
        Optional<EmergencyVehicle> available = vehicles.values().stream()
                .filter(EmergencyVehicle::isAvailable)
                .findFirst();

        if (available.isPresent()) {
            EmergencyVehicle vehicle = available.get();
            vehicle.setAvailable(false);

            // Start a thread that simulates the vehicle moving and requesting priority
            Thread priorityThread = new Thread(() -> {
                System.out.printf("[Dispatcher] %s %s en route to %s with priority request.\n",
                        vehicle.getType(), vehicle.getVehicleId(), accident.getLocation());

                for (String signalId : routeSignals) {
                    try {
                        System.out.printf("[%s %s] Approaching signal %s. Requesting priority...\n",
                                vehicle.getType(), vehicle.getVehicleId(), signalId);
                        String response = controlCenter.requestEmergencyPriority(
                                vehicle.getVehicleId(), signalId, "TO_ACCIDENT");
                        System.out.printf("[%s %s] %s\n", vehicle.getType(), vehicle.getVehicleId(), response);

                        // Simulate travel time between intersections (3 seconds each)
                        Thread.sleep(3000);
                    } catch (RemoteException e) {
                        System.err.printf("[%s %s] RMI error requesting priority: %s\n",
                                vehicle.getType(), vehicle.getVehicleId(), e.getMessage());
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                System.out.printf("[Dispatcher] %s %s reached accident location. Vehicle is now busy.\n",
                        vehicle.getType(), vehicle.getVehicleId());

                // Simulate time spent at scene (10 seconds)
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}

                // Make vehicle available again
                vehicle.setAvailable(true);
                System.out.printf("[Dispatcher] %s %s is now available for new emergencies.\n",
                        vehicle.getType(), vehicle.getVehicleId());
            });
            priorityThread.start();

            return String.format("Dispatched %s %s to %s with traffic priority over route: %s",
                    vehicle.getType(), vehicle.getVehicleId(), accident.getLocation(),
                    String.join(" -> ", routeSignals));
        } else {
            return "No emergency vehicles available. Accident queued.";
        }
    }

    /**
     * Simple dispatch that uses a default route (SIG_001, SIG_002, SIG_003)
     * for demonstration.
     */
    public String dispatchWithPriorityRoute(Accident accident) {
        String[] defaultRoute = {"SIG_001", "SIG_002", "SIG_003"};
        return dispatchEmergencyVehicleWithPriority(accident, defaultRoute);
    }

    /**
     * Returns a list of vehicle IDs and their types (for admin)
     */
    public String getVehicleStatus() {
        StringBuilder sb = new StringBuilder();
        for (EmergencyVehicle v : vehicles.values()) {
            sb.append(String.format("%s %s - Available: %s\n",
                    v.getType(), v.getVehicleId(), v.isAvailable() ? "YES" : "NO"));
        }
        return sb.toString();
    }
}
// com/smartcity/rpc/TrafficService.java
package com.smartcity.rpc;

import com.smartcity.model.Accident;
import com.smartcity.model.TrafficSignal;
import com.smartcity.service.AccidentLogger;
import com.smartcity.service.EmergencyDispatcher;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class TrafficService {
    private static final Logger logger = Logger.getLogger("TrafficService");
    private ConcurrentHashMap<String, TrafficSignal> trafficSignals = new ConcurrentHashMap<>();
    private AccidentLogger accidentLogger;
    private EmergencyDispatcher dispatcher;

    public TrafficService() {
        accidentLogger = new AccidentLogger();
        dispatcher = new EmergencyDispatcher();
        // Initialize traffic signals
        trafficSignals.put("SIG_001", new TrafficSignal("SIG_001", "40.7128,-74.0060", "GREEN"));
        trafficSignals.put("SIG_002", new TrafficSignal("SIG_002", "40.7138,-74.0070", "RED"));
        trafficSignals.put("SIG_003", new TrafficSignal("SIG_003", "40.7148,-74.0080", "YELLOW"));
    }

    public String reportAccident(String locationCoords) {
        try {
            logger.info("RPC call: report_accident(" + locationCoords + ")");
            Accident accident = new Accident(locationCoords, System.currentTimeMillis());
            accidentLogger.logAccident(accident);
            String dispatchMsg = dispatcher.dispatchEmergencyVehicle(accident);
            return "Accident reported at " + locationCoords + ". " + dispatchMsg;
        } catch (Exception e) {
            logger.severe("Error reporting accident: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public String updateTrafficSignal(String signalId, String state) {
        try {
            logger.info("RPC call: update_traffic_signal(" + signalId + ", " + state + ")");
            if (!trafficSignals.containsKey(signalId)) {
                return "Error: Signal ID not found";
            }
            if (!state.equals("RED") && !state.equals("GREEN") && !state.equals("YELLOW")) {
                return "Error: Invalid state. Use RED, GREEN, or YELLOW";
            }
            TrafficSignal signal = trafficSignals.get(signalId);
            signal.setState(state);
            trafficSignals.put(signalId, signal);
            return "Signal " + signalId + " updated to " + state;
        } catch (Exception e) {
            logger.severe("Error updating signal: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public TrafficSignal getSignal(String signalId) {
        return trafficSignals.get(signalId);
    }

    public ConcurrentHashMap<String, TrafficSignal> getAllSignals() {
        return trafficSignals;
    }
}

package com.smartcity.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Create RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1000);
            ControlCenterImpl controlCenter = new ControlCenterImpl();
            registry.rebind("ControlCenter", controlCenter);
            System.out.println("[RMIServer] Control Center bound. Registry on port 1000");

            // Create and register traffic sensors
            TrafficSensorImpl sensor1 = new TrafficSensorImpl("SENSOR_01", "Main Street & 5th Ave", controlCenter);
            TrafficSensorImpl sensor2 = new TrafficSensorImpl("SENSOR_02", "Broadway & 42nd St", controlCenter);
            TrafficSensorImpl sensor3 = new TrafficSensorImpl("SENSOR_03", "Market Street & 2nd St", controlCenter);

            controlCenter.registerSensor(sensor1);
            controlCenter.registerSensor(sensor2);
            controlCenter.registerSensor(sensor3);

            // Start all sensors' monitoring threads
            sensor1.startMonitoring();
            sensor2.startMonitoring();
            sensor3.startMonitoring();

            System.out.println("[RMIServer] All sensors registered and monitoring started.");
            System.out.println("[RMIServer] Running forever... (press Ctrl+C to stop)");

            // Keep server alive
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            System.err.println("RMIServer error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
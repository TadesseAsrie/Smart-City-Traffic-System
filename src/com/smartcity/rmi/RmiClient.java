// com/smartcity/rmi/RmiClient.java
package com.smartcity.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RmiClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ControlCenterRemote controlCenter = (ControlCenterRemote) registry.lookup("ControlCenter");

            // Create and register sensors
            TrafficSensorImpl sensor1 = new TrafficSensorImpl("SENSOR_01", "Main Street & 5th Ave", controlCenter);
            TrafficSensorImpl sensor2 = new TrafficSensorImpl("SENSOR_02", "Broadway & 42nd St", controlCenter);
            TrafficSensorImpl sensor3 = new TrafficSensorImpl("SENSOR_03", "Market Street & 2nd St", controlCenter);

            controlCenter.registerSensor(sensor1);
            controlCenter.registerSensor(sensor2);
            controlCenter.registerSensor(sensor3);

            // Start monitoring
            sensor1.startMonitoring();
            sensor2.startMonitoring();
            sensor3.startMonitoring();

            // Interactive menu
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== RMI Client Menu ===");
                System.out.println("1. View Traffic Status");
                System.out.println("2. Exit");
                System.out.print("Choice: ");

                int choice = scanner.nextInt();
                if (choice == 1) {
                    System.out.println(controlCenter.viewTrafficStatus());
                } else if (choice == 2) {
                    break;
                }
            }
            scanner.close();

        } catch (Exception e) {
            System.err.println("RMI Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
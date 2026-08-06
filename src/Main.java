

import com.smartcity.rmi.*;
import com.smartcity.rpc.*;
import com.smartcity.service.EmergencyDispatcher;
import com.smartcity.service.TrafficDashboard;
import com.smartcity.model.Accident;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Main entry point for the Smart City Traffic & Emergency Management System.
 * Starts RMI registry, RPC server, sensors, dashboard, and provides interactive menu.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // 1. Start RMI Registry
            Registry registry = LocateRegistry.createRegistry(1099);
            ControlCenterImpl controlCenter = new ControlCenterImpl();
            registry.rebind("ControlCenter", controlCenter);
            System.out.println("[Main] RMI Registry started on port 1099");

            // 2. Start RPC Server in a separate thread
            RpcServer rpcServer = new RpcServer(8080);
            Thread rpcThread = new Thread(() -> rpcServer.start());
            rpcThread.setDaemon(true);
            rpcThread.start();
            System.out.println("[Main] RPC Server started on port 8080");

            // 3. Allow time for servers to initialize
            Thread.sleep(1000);

            // 4. Create RPC client stub (used for menu options 1 & 2)
            RpcClientStub rpcClient = new RpcClientStub("localhost", 8080);

            // 5. Create TrafficService instance (used by dashboard to read signal states)
            //    Note: In real deployment, the RPC server already has its own TrafficService.
            //    We create a separate one only for dashboard display – this is fine for demo.
            TrafficService trafficService = new TrafficService();

            // 6. Create and start the dashboard (updates every 10 seconds by default)
            TrafficDashboard dashboard = new TrafficDashboard(controlCenter, trafficService);
            Thread dashboardThread = new Thread(dashboard);
            dashboardThread.setDaemon(true);
            dashboardThread.start();

            // 7. Create and register traffic sensors
            TrafficSensorImpl sensor1 = new TrafficSensorImpl("SENSOR_01", "Main Street & 5th Ave", controlCenter);
            TrafficSensorImpl sensor2 = new TrafficSensorImpl("SENSOR_02", "Broadway & 42nd St", controlCenter);
            TrafficSensorImpl sensor3 = new TrafficSensorImpl("SENSOR_03", "Market Street & 2nd St", controlCenter);

            controlCenter.registerSensor(sensor1);
            controlCenter.registerSensor(sensor2);
            controlCenter.registerSensor(sensor3);

            // Start monitoring threads for each sensor
            sensor1.startMonitoring();
            sensor2.startMonitoring();
            sensor3.startMonitoring();

            // 8. Initialize Emergency Dispatcher and pass control center reference
            EmergencyDispatcher dispatcher = new EmergencyDispatcher();
            dispatcher.setControlCenter(controlCenter);

            // 9. Interactive menu
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n=== SMART CITY TRAFFIC MANAGEMENT SYSTEM ===");
            System.out.println("RPC Server: localhost:8080");
            System.out.println("RMI Registry: localhost:1099");
            System.out.println("Dashboard updates every 10 seconds\n");

            while (true) {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Report Accident (RPC) - basic dispatch");
                System.out.println("2. Update Traffic Signal (RPC)");
                System.out.println("3. View All Traffic Status (RMI)");
                System.out.println("4. Get Report from Specific Sensor (RMI)");
                System.out.println("5. Report Accident with PRIORITY route (uses RMI priority)");
                System.out.println("6. Manually Request Emergency Priority at a Signal (RMI)");
                System.out.println("7. Exit");
                System.out.print("Choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1: // Basic accident report (no priority)
                        System.out.print("Enter location coordinates: ");
                        String location = scanner.nextLine();
                        try {
                            String result = rpcClient.reportAccident(location);
                            System.out.println("Response: " + result);
                        } catch (Exception e) {
                            System.err.println("RPC Error: " + e.getMessage());
                        }
                        break;

                    case 2: // Update traffic signal via RPC
                        System.out.print("Enter signal ID (SIG_001, SIG_002, SIG_003): ");
                        String signalId = scanner.nextLine();
                        System.out.print("Enter state (RED/GREEN/YELLOW): ");
                        String state = scanner.nextLine();
                        try {
                            String result = rpcClient.updateTrafficSignal(signalId, state);
                            System.out.println("Response: " + result);
                        } catch (Exception e) {
                            System.err.println("RPC Error: " + e.getMessage());
                        }
                        break;

                    case 3: // View all traffic status (RMI)
                        try {
                            String status = controlCenter.viewTrafficStatus();
                            System.out.println(status);
                        } catch (Exception e) {
                            System.err.println("RMI Error: " + e.getMessage());
                        }
                        break;

                    case 4: // Get report from a specific sensor
                        System.out.print("Enter Sensor ID (SENSOR_01, SENSOR_02, SENSOR_03): ");
                        String sensorId = scanner.nextLine();
                        try {
                            String report = controlCenter.getSensorReport(sensorId);
                            System.out.println(report);
                        } catch (Exception e) {
                            System.err.println("RMI Error: " + e.getMessage());
                        }
                        break;

                    case 5: // Report accident with PRIORITY (uses dispatcher with priority)
                        System.out.print("Enter accident location coordinates: ");
                        String accidentLoc = scanner.nextLine();
                        Accident accident = new Accident(accidentLoc, System.currentTimeMillis());
                        String dispatchResult = dispatcher.dispatchWithPriorityRoute(accident);
                        System.out.println("Dispatch Result: " + dispatchResult);
                        break;

                    case 6: // Manually simulate emergency vehicle priority at any signal
                        System.out.print("Enter vehicle ID (e.g., AMB_001, FIRE_001, POL_001): ");
                        String vehId = scanner.nextLine();
                        System.out.print("Enter signal ID (SIG_001, SIG_002, SIG_003): ");
                        String sigId = scanner.nextLine();
                        System.out.print("Enter direction (NORTH/SOUTH/EAST/WEST): ");
                        String dir = scanner.nextLine();
                        try {
                            String result = controlCenter.requestEmergencyPriority(vehId, sigId, dir);
                            System.out.println(result);
                        } catch (Exception e) {
                            System.err.println("RMI Error: " + e.getMessage());
                        }
                        break;

                    case 7: // Exit
                        System.out.println("Shutting down system...");
                        scanner.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter 1-7.");
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error in Main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//// com/smartcity/rmi/ControlCenterRemote.java
//package com.smartcity.rmi;
//
//import java.rmi.Remote;
//import java.rmi.RemoteException;
//import java.util.List;
//
//public interface ControlCenterRemote extends Remote {
//    void receiveAlert(String sensorId, int congestionLevel, String location) throws RemoteException;
//    void registerSensor(TrafficSensorRemote sensor) throws RemoteException;
//    String viewTrafficStatus() throws RemoteException;
//}
package com.smartcity.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ControlCenterRemote extends Remote {
    void receiveAlert(String sensorId, int congestionLevel, String location) throws RemoteException;
    void registerSensor(TrafficSensorRemote sensor) throws RemoteException;
    String viewTrafficStatus() throws RemoteException;
    String getSensorReport(String sensorId) throws RemoteException;

    // Emergency priority methods
    String requestEmergencyPriority(String emergencyVehicleId, String signalId, String direction) throws RemoteException;
    void releaseEmergencyPriority(String signalId) throws RemoteException;

    // Handle congestion by adjusting signals
    void handleCongestion(String sensorId, int congestionLevel) throws RemoteException;
}
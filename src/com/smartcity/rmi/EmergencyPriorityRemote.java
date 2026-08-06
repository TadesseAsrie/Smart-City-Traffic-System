package com.smartcity.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EmergencyPriorityRemote extends Remote {
    // Emergency vehicle requests priority at a specific signal
    String requestPriority(String emergencyVehicleId, String signalId, String direction) throws RemoteException;

    // Release priority after emergency vehicle passes
    void releasePriority(String signalId) throws RemoteException;

    // Get current priority status
    String getPriorityStatus(String signalId) throws RemoteException;
}
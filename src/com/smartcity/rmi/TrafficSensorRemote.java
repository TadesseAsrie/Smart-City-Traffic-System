// com/smartcity/rmi/TrafficSensorRemote.java
package com.smartcity.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TrafficSensorRemote extends Remote {
    String getSensorId() throws RemoteException;
    String getLocation() throws RemoteException;
    int detectTraffic() throws RemoteException;
    void sendCongestionAlert(int congestionLevel) throws RemoteException;
}
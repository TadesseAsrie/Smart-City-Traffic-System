// com/smartcity/rmi/TrafficSensorImpl.java
package com.smartcity.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficSensorImpl extends UnicastRemoteObject implements TrafficSensorRemote {
    private String sensorId;
    private String location;
    private int currentCongestion;
    private ControlCenterRemote controlCenter;
    private Random random;
    private AtomicInteger congestionHistory;

    public TrafficSensorImpl(String sensorId, String location, ControlCenterRemote controlCenter)
            throws RemoteException {
        super();
        this.sensorId = sensorId;
        this.location = location;
        this.controlCenter = controlCenter;
        this.currentCongestion = 0;
        this.random = new Random();
        this.congestionHistory = new AtomicInteger(0);
    }

    @Override
    public String getSensorId() throws RemoteException {
        return sensorId;
    }

    @Override
    public String getLocation() throws RemoteException {
        return location;
    }

    @Override
    public int detectTraffic() throws RemoteException {
        int newCongestion = random.nextInt(101); // 0-100 congestion level
        if (Math.abs(newCongestion - currentCongestion) > 20) {
            currentCongestion = newCongestion;
            sendCongestionAlert(currentCongestion);
        }
        return currentCongestion;
    }

    @Override
    public void sendCongestionAlert(int congestionLevel) throws RemoteException {
        System.out.println("[Sensor " + sensorId + "] Congestion alert: " + congestionLevel + "% at " + location);
        controlCenter.receiveAlert(sensorId, congestionLevel, location);
    }

    public void startMonitoring() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Monitor every 5 seconds
                    detectTraffic();
                } catch (InterruptedException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
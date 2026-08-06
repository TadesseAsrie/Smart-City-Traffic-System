// com/smartcity/service/AccidentLogger.java
package com.smartcity.service;

import com.smartcity.model.Accident;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AccidentLogger {
    private static final String LOG_FILE = "accidents.log";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized void logAccident(Accident accident) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            String logEntry = String.format("%s | Location: %s | Status: %s\n",
                    sdf.format(new Date(accident.getTimestamp())),
                    accident.getLocation(),
                    accident.getStatus());
            bw.write(logEntry);
            System.out.println("[AccidentLogger] Accident logged: " + logEntry);
        } catch (IOException e) {
            System.err.println("Error logging accident: " + e.getMessage());
        }
    }

    public List<Accident> readAccidents() {
        List<Accident> accidents = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("LOG: " + line);
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return accidents;
    }
}
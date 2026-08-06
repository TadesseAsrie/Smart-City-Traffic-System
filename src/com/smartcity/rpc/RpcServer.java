// com/smartcity/rpc/RpcServer.java
package com.smartcity.rpc;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class RpcServer {
    private static final Logger logger = Logger.getLogger("RpcServer");
    private int port;
    private TrafficService service;
    private ExecutorService threadPool;
    private volatile boolean running;

    public RpcServer(int port) {
        this.port = port;
        this.service = new TrafficService();
        this.threadPool = Executors.newCachedThreadPool();
        this.running = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("RPC Server started on port " + port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: " + clientSocket.getInetAddress());
                threadPool.submit(new RpcHandler(clientSocket, service));
            }
        } catch (IOException e) {
            logger.severe("Server error: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        threadPool.shutdown();
    }

    public static void main(String[] args) {
        RpcServer server = new RpcServer(8070);
        server.start();
    }
}

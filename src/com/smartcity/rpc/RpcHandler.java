package com.smartcity.rpc;

import java.io.*;
import java.net.Socket;
import java.lang.reflect.Method;

public class RpcHandler implements Runnable {
    private Socket clientSocket;
    private TrafficService service;

    public RpcHandler(Socket socket, TrafficService service) {
        this.clientSocket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            RpcRequest request = (RpcRequest) in.readObject();
            RpcResponse response = processRequest(request);
            out.writeObject(response);
            out.flush();

        } catch (Exception e) {
            System.err.println("Error handling RPC request: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException e) {}
        }
    }

    private RpcResponse processRequest(RpcRequest request) {
        try {
            String methodName = request.getMethodName();
            Object[] params = request.getParams();

            // Find method by name and parameter count (all params are String)
            Method method = null;
            for (Method m : TrafficService.class.getMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == params.length) {
                    method = m;
                    break;
                }
            }

            if (method == null) {
                throw new NoSuchMethodException("No method: " + methodName + " with " + params.length + " parameters");
            }

            // Invoke the method with the parameters
            Object result = method.invoke(service, params);
            return new RpcResponse(result, request.getRequestId());

        } catch (Exception e) {
            return new RpcResponse(e, request.getRequestId());
        }
    }
}
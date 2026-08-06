// com/smartcity/rpc/RpcClientStub.java
package com.smartcity.rpc;

import java.io.*;
import java.net.*;

public class RpcClientStub {
    private String serverHost;
    private int serverPort;

    public RpcClientStub(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String reportAccident(String locationCoords) throws Exception {
        RpcRequest request = new RpcRequest("reportAccident", new Object[]{locationCoords});
        RpcResponse response = sendRequest(request);
        if (response.isError()) {
            throw response.getError();
        }
        return (String) response.getResult();
    }

    public String updateTrafficSignal(String signalId, String state) throws Exception {
        RpcRequest request = new RpcRequest("updateTrafficSignal", new Object[]{signalId, state});
        RpcResponse response = sendRequest(request);
        if (response.isError()) {
            throw response.getError();
        }
        return (String) response.getResult();
    }

    private RpcResponse sendRequest(RpcRequest request) throws Exception {
        try (Socket socket = new Socket(serverHost, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();
            return (RpcResponse) in.readObject();
        }
    }
}

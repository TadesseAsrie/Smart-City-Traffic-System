package com.smartcity.rpc;

public class RPCServerMain {
    public static void main(String[] args) {
        RpcServer server = new RpcServer(8080);
        System.out.println("[RPCServer] Starting RPC server on port 8080...");
        server.start(); // runs forever
    }
}
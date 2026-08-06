package com.smartcity.rpc;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String methodName;
    private Object[] params;
    private String requestId;

    public RpcRequest(String methodName, Object[] params) {
        this.methodName = methodName;
        this.params = params;
        this.requestId = java.util.UUID.randomUUID().toString();
    }

    public String getMethodName() { return methodName; }
    public Object[] getParams() { return params; }
    public String getRequestId() { return requestId; }
}
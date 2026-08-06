package com.smartcity.rpc;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Object result;
    private Exception error;
    private String requestId;

    public RpcResponse(Object result, String requestId) {
        this.result = result;
        this.requestId = requestId;
        this.error = null;
    }

    public RpcResponse(Exception error, String requestId) {
        this.error = error;
        this.requestId = requestId;
        this.result = null;
    }

    public Object getResult() { return result; }
    public Exception getError() { return error; }
    public String getRequestId() { return requestId; }
    public boolean isError() { return error != null; }
}
package com.hoper.rpc.client.proxy;

import java.io.Serializable;

public class RemoteTO implements Serializable {
    private String serviceName;
    private String method;
    private Class[] paramTypes;
    private Object[] args;

    public RemoteTO(String serviceName, String method, Class[] paramTypes, Object[] args) {
        this.serviceName = serviceName;
        this.method = method;
        this.paramTypes = paramTypes;
        this.args = args;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethod() {
        return method;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }
}

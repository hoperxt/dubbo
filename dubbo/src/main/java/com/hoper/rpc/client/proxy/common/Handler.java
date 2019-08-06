package com.hoper.rpc.client.proxy.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Handler implements InvocationHandler {

    private Object subject;

    public Handler(Object subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        Object returnValue  = method.invoke(subject, args);
        System.out.println("after");
        return returnValue ;
    }
}

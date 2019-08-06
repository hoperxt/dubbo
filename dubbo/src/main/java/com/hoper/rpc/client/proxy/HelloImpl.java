package com.hoper.rpc.client.proxy;

public class HelloImpl implements Hello {
    @Override
    public void hello(String name) {
        System.out.println("hello," + name);
    }
}

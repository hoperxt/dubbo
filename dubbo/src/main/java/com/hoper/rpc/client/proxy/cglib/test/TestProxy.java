package com.hoper.rpc.client.proxy.cglib.test;

import com.hoper.rpc.client.proxy.service.Hello2;
import com.hoper.rpc.client.proxy.cglib.ProxyCglibFactory;

/**
 * 通过cglib生成代理对象，被代理的对象不需要实现一个或多个接口，即Hello2不需要实现任何接口
 * 这样对于Hello2来说是无侵入的
 */
public class TestProxy {
    public static void main(String[] args) {
        Hello2 hello2 = new Hello2();
        System.out.println(hello2.getClass());

        Hello2 proxy = (Hello2) new ProxyCglibFactory(new Hello2()).getProxyInstance();
        System.out.println(proxy.getClass());
        proxy.sayHello("java");
    }
}

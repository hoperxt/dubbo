package com.hoper.rpc.client.proxy.common.test;

import com.hoper.rpc.client.proxy.Hello;
import com.hoper.rpc.client.proxy.HelloImpl;
import com.hoper.rpc.client.proxy.common.ProxyFactory;

/**
 * 静态代理在编译时就已经实现，编译完成后代理类是一个实际的class文件
 * 动态代理是在运行时动态生成的，即编译完成后没有实际的class文件，而是在运行时动态生成类字节码，并加载到JVM中
 */
public class ProxyTest {
    public static void main(String[] args) {
        Hello hello = new HelloImpl();
        System.out.println(hello.getClass());   //class com.hoper.rpc.client.proxy.HelloImpl

        Hello proxy = (Hello) new ProxyFactory(new HelloImpl()).getProxyInstance();
        System.out.println(proxy.getClass());   //class com.sun.proxy.$Proxy0
        proxy.hello("java");
    }
}

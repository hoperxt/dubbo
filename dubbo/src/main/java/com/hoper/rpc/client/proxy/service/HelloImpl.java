package com.hoper.rpc.client.proxy.service;

public class HelloImpl implements Hello {
    @Override
    public void hello(String name) {
        System.out.println("hello," + name);
    }

    //这个方法不会在代理类中生成（通过Proxy.newProxyInstance创建的代理类），可以把代理类的.class保存到硬盘，然后反编译打开
    public void hi(String name) {
        System.out.println("hi," + name);
    }
}

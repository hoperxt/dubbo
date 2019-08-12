package com.hoper.rpc.client.proxy.common.test;

import com.hoper.rpc.client.proxy.service.Hello;
import com.hoper.rpc.client.proxy.service.HelloImpl;
import com.hoper.rpc.client.proxy.common.Handler;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * 静态代理在编译时就已经实现，编译完成后代理类是一个实际的class文件
 * 动态代理是在运行时动态生成的，即编译完成后没有实际的class文件，而是在运行时动态生成类字节码，并加载到JVM中
 * <p>
 * https://www.cnblogs.com/rinack/p/7742682.html
 */
public class ProxyTest {
    public static void main(String[] args) {
        HelloImpl hello = new HelloImpl();
        System.out.println(hello.getClass());   //class com.hoper.rpc.client.proxy.service.HelloImpl

        //获取对应的classLoader
        ClassLoader cl = hello.getClass().getClassLoader();
        //获取hello所实现的所有接口
        Class[] interfaces = hello.getClass().getInterfaces();
        Handler handler = new Handler(hello);
        /*
            jdk根据传入的参数信息，动态地在内存中创建和.class文件等同的字节码
            然后根据相应的字节码转换成对应的class
            然后调用newInstance()创建实例
         */
        Hello proxy = (Hello) Proxy.newProxyInstance(cl, interfaces, handler);
        System.out.println(proxy.getClass());   //class com.sun.proxy.$Proxy0
        proxy.hello("java");
        generateClassFile(hello.getClass(),"HelloProxy");
    }

    /**
     * 将对象保存到硬盘上
     *
     * @param clazz
     * @param proxyName
     */
    public static void generateClassFile(Class clazz, String proxyName) {
        byte[] classFile = ProxyGenerator.generateProxyClass(proxyName, clazz.getInterfaces());
        String paths = clazz.getResource(".").getPath();
        System.out.println(paths);
        FileOutputStream out = null;
        try {
            //保留到硬盘中
            out = new FileOutputStream(paths + proxyName + ".class");
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

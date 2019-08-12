package com.hoper.rpc.client;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hoper.rpc.client.proxy.RemoteTO;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RPCUtils<T> {
    //使用传统的方式生成代理对象
    public static Object getInstance(Class clz) {
        return Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, (proxy, method, args) -> call(new InetSocketAddress(8000), clz.getName(), method, args));
    }

    //使用cglib生成代理对象
    public static Object getInstance2(Class clz) {
        Enhancer en = new Enhancer();
        en.setSuperclass(clz);
        en.setCallback((MethodInterceptor) (o, method, args, methodProxy) -> call2(new InetSocketAddress(8001), clz.getName(), method, args));
        return en.create();
    }

    //使用jdk自带的序列化工具，被序列化的对象必须实现Serializable接口，以告知其可被序列化
    private static Object call(InetSocketAddress address, String serviceName, Method method, Object[] args) {
        Socket socket = new Socket();
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            socket.connect(address);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeUTF(serviceName);                       //序列化serviceName到socket.getOutputStream()中
            output.writeUTF(method.getName());
            output.writeObject(method.getParameterTypes());
            output.writeObject(args);
            input = new ObjectInputStream(socket.getInputStream());
            return input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(socket, output, input);
        }
        return null;
    }

    private static Object call2(InetSocketAddress address, String serviceName, Method method, Object[] args) {
        Socket socket = new Socket();
        Output output = null;
        Input input = null;
        Kryo kryo = new Kryo();
        try {
            socket.connect(address);
            output = new Output(socket.getOutputStream());
            kryo.writeObject(output, new RemoteTO(serviceName, method.getName(), method.getParameterTypes(), args));
            input = new Input(socket.getInputStream());
            return kryo.readObject(input, String.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(socket, output, input);
        }
        return null;
    }

    private static void closeResource(Socket socket, OutputStream output, InputStream input) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

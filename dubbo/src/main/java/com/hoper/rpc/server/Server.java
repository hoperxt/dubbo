package com.hoper.rpc.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hoper.rpc.client.proxy.RemoteTO;
import com.hoper.rpc.server.service.HelloService;
import com.hoper.rpc.server.service.HelloServiceImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final HashMap<String, Class> serviceRegistry = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        serviceRegistry.put(HelloService.class.getName(), HelloServiceImpl.class);
        server.start1();
        server.start2();
    }

    public void start1() {
        new Thread(() -> {
            ServerSocket server = null;
            try {
                server = new ServerSocket();
                server.bind(new InetSocketAddress(8000));
                System.out.println("Server start1!");
                while (true) {
                    Socket socket = server.accept();//阻塞线程，一直等待直到有客户端连接，可以考虑使用NIO模式
                    executor.execute(new ServiceTask(socket));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void start2() {
        new Thread(() -> {
            ServerSocket server = null;
            try {
                server = new ServerSocket();
                server.bind(new InetSocketAddress(8001));
                System.out.println("Server start2!");
                while (true) {
                    Socket socket = server.accept();//阻塞线程，一直等待直到有客户端连接，可以考虑使用NIO模式
                    executor.execute(new ServiceTask2(socket));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static Object invoke(String serviceName, String methodName, Class<?>[] params, Object[] args) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class serviceClass = serviceRegistry.get(serviceName);
        if (serviceClass == null) {
            throw new ClassNotFoundException(serviceName + " not found");
        }
        Method method = serviceClass.getMethod(methodName, params);
        return method.invoke(serviceClass.newInstance(), args);
    }

    private static class ServiceTask2 implements Runnable {

        Socket client;

        public ServiceTask2(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            Kryo kryo = new Kryo();
            Output output = null;
            Input input = null;
            try {
                input = new Input(client.getInputStream());
                RemoteTO to = kryo.readObject(input, RemoteTO.class);
                String serviceName = to.getServiceName();
                String methodName = to.getMethod();
                Class<?>[] params = to.getParamTypes();
                Object[] args = to.getArgs();
                Object result = invoke(serviceName, methodName, params, args);
                kryo.writeObject(output, result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeResource(client, output, input);
            }
        }

    }

    private static class ServiceTask implements Runnable {
        Socket client;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                input = new ObjectInputStream(client.getInputStream());
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] params = (Class<?>[]) input.readObject();
                Object[] args = (Object[]) input.readObject();
                Object result = invoke(serviceName, methodName, params, args);
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeResource(client, output, input);
            }
        }
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

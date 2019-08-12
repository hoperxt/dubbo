package com.hoper.rpc.client;

import com.hoper.rpc.server.service.HelloService;

public class Client {
    public static void main(String[] args) {
        HelloService service = (HelloService) RPCUtils.getInstance2(HelloService.class);
        String result = service.sayHi("java");
        System.out.println(result);
    }
}

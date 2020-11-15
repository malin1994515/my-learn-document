package com.dubbo.learn;

public class SayHelloServiceImpl implements ISayHelloService {
    @Override
    public String sayHello(String msg) {
        return "Hello: " + msg;
    }
}

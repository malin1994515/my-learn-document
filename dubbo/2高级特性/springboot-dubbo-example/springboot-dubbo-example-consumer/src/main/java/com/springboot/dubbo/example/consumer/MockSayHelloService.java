package com.springboot.dubbo.example.consumer;

import com.springboot.dubbo.example.api.ISayHelloService;

public class MockSayHelloService implements ISayHelloService {
    @Override
    public String sayHell() {
        return "触发服务降级";
    }
}

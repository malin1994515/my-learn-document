package com.springcloud.dubbo.example.provider.service;

import com.springcloud.dubbo.example.api.ISayHello;
import org.apache.dubbo.config.annotation.Service;

@Service
public class SayHelloServiceImpl implements ISayHello {
    @Override
    public String sayHello() {
        return "Hello World";
    }
}

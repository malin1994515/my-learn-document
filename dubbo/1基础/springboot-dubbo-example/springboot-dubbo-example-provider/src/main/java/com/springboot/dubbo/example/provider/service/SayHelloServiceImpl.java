package com.springboot.dubbo.example.provider.service;

import com.springboot.dubbo.example.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(registry = {"shanghai", "hubei"}, version = "1.0")
public class SayHelloServiceImpl implements ISayHelloService {
    @Override
    public String sayHell() {
        System.out.println("11");
        return "[Version1.0]Hello World";
    }
}

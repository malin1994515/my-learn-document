package com.springboot.dubbo.example.provider.service;

import com.springboot.dubbo.example.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(registry = {"shanghai", "hubei"}, version = "2.0")
public class SayHelloServiceImpl2 implements ISayHelloService {
    @Override
    public String sayHell() {
        System.out.println("11");
        return "[Version2.0]Hello World";
    }
}

package com.springboot.dubbo.example.provider.service.impl;

import com.springboot.dubbo.example.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(registry = {"shanghai", "hubei"}, protocol = {"dubbo", "rest"}, loadbalance = "leastactive")
public class SayHelloServiceImpl implements ISayHelloService {

    @Override
    public String sayHell() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("1111");
        return "[Version1.0]Hello World";
    }
}

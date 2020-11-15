package com.springcloud.nacos.demo.springcloudnacosdemo.dubbo.provider;

import com.springcloud.nacos.demo.springcloudnacosdemo.dubbo.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(protocol = "dubbo")
public class SayHelloServiceImpl implements ISayHelloService {
    @Override
    public String sayHello() {
        return "Hello World";
    }
}

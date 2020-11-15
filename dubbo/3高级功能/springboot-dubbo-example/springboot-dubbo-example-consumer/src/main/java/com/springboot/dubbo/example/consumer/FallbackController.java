package com.springboot.dubbo.example.consumer;

import com.springboot.dubbo.example.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @DubboReference(registry = {"shanghai", "hubei"},
            protocol = "dubbo",
            loadbalance = "consistenthash",
            mock = "com.springboot.dubbo.example.consumer.MockSayHelloService",
            timeout = 500,
            cluster = "failfast",
            check = true)
    private ISayHelloService sayHelloService;

    @GetMapping("/say")
    public String say() {
        return sayHelloService.sayHell();
    }
}

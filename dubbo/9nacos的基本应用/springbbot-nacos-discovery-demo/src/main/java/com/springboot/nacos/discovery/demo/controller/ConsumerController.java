package com.springboot.nacos.discovery.demo.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @NacosInjected
    private NamingService namingService;

    private RestTemplate restTemplate = new RestTemplate();
    private String serviceName = "demo-application";

    @GetMapping("/demo")
    public String consumer() throws NacosException {
        // 1. 获得实例
        Instance instance = null;
        if (false) {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            // 获得首个实例，进行调用
            instance = instances.stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("未找到对应的 Instance"));
        } else {
            instance = namingService.selectOneHealthyInstance(serviceName);
        }

        // 2. 执行请求
        String url = "http://".concat(instance.toInetAddr()).concat("/provider/demo");
        return restTemplate.getForObject(url, String.class);
    }
}

package com.springcloud.nacos.discovery.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Configuration
    public class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @RestController
    public class TestController {
        @Resource
        private DiscoveryClient discoveryClient;
        @Resource
        private RestTemplate restTemplate;
        @Resource
        private LoadBalancerClient loadBalancerClient;

        @GetMapping("/hello")
        public String hello(String name) {
            // 1. 获得`demo-provider`实例
            ServiceInstance instance;
            if (true) {
                // 获得`demo-provider`服务列表
                List<ServiceInstance> instances = discoveryClient.getInstances("demo-provider");
                // 选择第一个
                instance = instances.size() > 0 ? instances.get(0) : null;
            } else {
                instance = loadBalancerClient.choose("demo-provider");
            }
            // 2. 发起调用
            if (instance == null) throw new IllegalStateException("获取不到实例");
            String targetUrl = instance.getUri() + "/echo?name=" + name;
            String response = restTemplate.getForObject(targetUrl, String.class);
            return "consumer:" + response;
        }
    }
}

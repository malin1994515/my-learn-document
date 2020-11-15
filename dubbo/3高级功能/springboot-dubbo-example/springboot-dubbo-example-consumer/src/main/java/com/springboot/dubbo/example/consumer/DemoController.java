package com.springboot.dubbo.example.consumer;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    @DubboReference(interfaceName = "com.springboot.dubbo.example.provider.service.IDemoService",
            generic = true)
    private GenericService demoService;

    @GetMapping("/demo")
    public String demo() {
        Map<String, Object> user = new HashMap<>();
        user.put("", ""); // key表达user对象中的属性，value表达属性的值
        return (String) demoService.$invoke("getTxt", new String[0], new Object[0]);
    }
}

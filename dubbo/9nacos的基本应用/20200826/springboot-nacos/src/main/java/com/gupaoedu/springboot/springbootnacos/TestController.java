package com.gupaoedu.springboot.springbootnacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
@NacosPropertySource(dataId = "springboot-nacos",autoRefreshed = true)
@RestController
public class TestController {

    @NacosValue(value = "${info:default value}",autoRefreshed = true)
    private String info;

    @GetMapping("/get")
    public String get(){
        return info;
    }

    @NacosInjected
    private NamingService namingService;

    @PostMapping("/registry")
    public String registry(){
        Instance instance=new Instance();
        namingService.registerInstance();
    }


}

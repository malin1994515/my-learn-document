package com.springboot.dubbo.example.provider.service.impl;

import com.springboot.dubbo.example.provider.service.IDemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(registry = {"shanghai", "hubei"}, protocol = {"dubbo"})
public class DemoService implements IDemoService {

    @Override
    public String getTxt() {
        return "泛化DemoAPI";
    }
}

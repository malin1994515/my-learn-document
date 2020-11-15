package com.gupaoedu.example.springcloudnacosdubboconsumer;


import com.gupaoedu.example.api.EchoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
@RestController
public class TestController {

    @Reference
    EchoService echoService;

    @GetMapping("/call")
    public String call(){
        return echoService.echo("Mic");
    }
}

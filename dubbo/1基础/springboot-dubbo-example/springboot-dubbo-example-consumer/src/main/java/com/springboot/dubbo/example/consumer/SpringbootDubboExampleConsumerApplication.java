package com.springboot.dubbo.example.consumer;

import com.springboot.dubbo.example.api.ISayHelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringbootDubboExampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDubboExampleConsumerApplication.class, args);
	}

	@DubboReference(registry = {"shanghai", "hubei"}, version = "1.0")
	private ISayHelloService sayHelloService;

	@GetMapping("get")
	public String get() {
		return sayHelloService.sayHell();
	}

}

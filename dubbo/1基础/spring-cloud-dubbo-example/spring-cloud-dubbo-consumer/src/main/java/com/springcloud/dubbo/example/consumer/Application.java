package com.springcloud.dubbo.example.consumer;

import com.springcloud.dubbo.example.api.ISayHello;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Reference
	private ISayHello sayHello;

	@GetMapping("/say")
	public String say() {
		return sayHello.sayHello();
	}
}

package com.dubbo.learn;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // ISayHelloService sayHelloService = null;
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/application.xml");
        ISayHelloService sayHelloService = context.getBean(ISayHelloService.class);
        System.out.println(sayHelloService.sayHello("hi"));
    }
}

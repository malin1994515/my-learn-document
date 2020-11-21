package com.springboot.kafka.demo;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 10; i++) {
            product(new User(i, "Malin" + i, i));
        }
    }

    private static String group = "uc";
    private static String topic = "user";

    @Resource
    private KafkaTemplate kafkaTemplate;


    public void product(User user) {
        kafkaTemplate.send(topic, JSON.toJSONString(user, true));
    }

    @KafkaListener(topics = "user")
    public void consumer(ConsumerRecord consumerRecord) {
        Object message = consumerRecord.value();
        System.out.println(message.toString());
    }
}

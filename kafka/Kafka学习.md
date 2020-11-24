# 学习文档

https://kafka.apachecn.org/

https://spring.io/projects/spring-kafka

# Docker 安装Kafka

## Linux

https://hub.docker.com/r/wurstmeister/kafka

```shell
docker-compose -f docker-compose-single-broker.yml up
```

## Window

https://www.jianshu.com/p/e57935a4027b

```shell
docker run -d --name zookeeper -p 2181 -t wurstmeister/zookeeper
docker run -d --name kafka --publish 9092:9092 --link zookeeper --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 --env KAFKA_ADVERTISED_HOST_NAME=localhost --env KAFKA_ADVERTISED_PORT=9092 --volume /etc/localtime:/etc/localtime wurstmeister/kafka:latest

```

```shell
docker run -d --name kafka --publish 9092:9092 --link zookeeper --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 --env KAFKA_ADVERTISED_HOST_NAME=localhost --env KAFKA_ADVERTISED_PORT=9092 wurstmeister/kafka:latest
```



https://blog.csdn.net/sayoko06/article/details/104020621

使用`docker-compose-windows.yml`

```shell
docker-compose -f docker-compose-windows.yml up
```

然后修改hosts

```tex
127.0.0.1 kafka.local
127.0.0.1 zookeeper.local
```



## 集成Spring

- https://blog.csdn.net/qq_30166123/article/details/89705385



# Spring Boot集成

https://github.com/spring-projects/spring-kafka

https://www.cnblogs.com/riches/p/11720068.html

https://www.cnblogs.com/tysl/p/11170811.html

## 配置文件

```yaml
server:
  port: 1000

spring:
  application:
    name: kafka-consumer

kafka:
    bootstrap:
      servsers: 127.0.0.1:9092
    topic:
      user: topic-user
    group:
      id: group-user
```



## 消息生产者

```java
@Slf4j
@Component
public class KafkaProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.user}")
    private String topicUser;

    public void sendMessage(User user) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        String message = builder.create().toJson(user);
        kafkaTemplate.send(topicUser, message);
        log.info("生产消息发送至Kafka: {}", message);
    }
}
```



```java
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource
    private KafkaProducer kafkaProducer;

    @RequestMapping("/createMsg")
    public void createMsg() {
        User user = new User();
        user.setId(1);
        user.setName("Malin");
        user.setAge(26);
        kafkaProducer.sendMessage(user);
    }
}
```



## 验证消息

```shell
C:\Users\datahome>docker exec -it kafka /bin/bash
bash-4.4# cd bin/
bash-4.4# kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic-user --from-beginning
{
  "id": 1,
  "name": "Malin",
  "age": 26
}
{
  "id": 1,
  "name": "Malin",
  "age": 26
}
{
  "id": 1,
  "name": "Malin",
  "age": 26
}
```



# Linux 安装 kafka 集群

https://kafka.apachecn.org/quickstart.html

整体感觉挺简单的，按照Quick Start 很快就搭建好了。



> 但是不知道为什么 执行命令 非常慢。可能是机器配置很低，没工夫换配置高的机器来验证。

# FAQ

Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.

- 使用`docker-compose-windows.yml` 创建桥接网络 `kafka.local` 和 `zookeeper.local`，需要修改`hosts` 添加

```tex
127.0.0.1 kafka.local
127.0.0.1 zookeeper.local
```



Git Clone 慢--可惜修改hosts无效，以前还有用的。

- https://zhuanlan.zhihu.com/p/188750084
- ipconfig /flushdns


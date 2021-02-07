# FAQ



## `ResponseBodyAdivce` 和 `ResponseEntityExceptionHandler`



Demo

```java
@Slf4j
@RestControllerAdvice
public class ResultResponseBodyAdvice extends ResponseEntityExceptionHandler implements ResponseBodyAdvice<Object> {
    
}
```



堆栈信息

```
2021-02-07 15:20:57.108  INFO 29056 --- [ost-startStop-1] ConditionEvaluationReportLoggingListener : 

Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2021-02-07 15:20:57.115 ERROR 29056 --- [ost-startStop-1] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'handlerExceptionResolver' defined in class path resource [org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.web.servlet.HandlerExceptionResolver]: Factory method 'handlerExceptionResolver' threw exception; nested exception is java.lang.IllegalStateException: Ambiguous @ExceptionHandler method mapped for [class org.springframework.web.bind.MethodArgumentNotValidException]: {public org.springframework.http.ResponseEntity com.datahome.activity.aop.ResultResponseBodyAdvice.handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException), public final org.springframework.http.ResponseEntity org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler.handleException(java.lang.Exception,org.springframework.web.context.request.WebRequest) throws java.lang.Exception}
	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:655) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:635) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1336) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1176) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:556) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:226) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:897) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:879) ~[spring-context-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:551) ~[spring-context-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:143) ~[spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:758) ~[spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:750) ~[spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397) ~[spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) ~[spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.run(SpringBootServletInitializer.java:173) [spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.createRootApplicationContext(SpringBootServletInitializer.java:153) [spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.onStartup(SpringBootServletInitializer.java:95) [spring-boot-2.3.3.RELEASE.jar:2.3.3.RELEASE]
	at org.springframework.web.SpringServletContainerInitializer.onStartup(SpringServletContainerInitializer.java:172) [spring-web-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5144) [catalina.jar:8.5.57]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183) [catalina.jar:8.5.57]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1412) [catalina.jar:8.5.57]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1402) [catalina.jar:8.5.57]
	at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266) [na:1.8.0_271]
	at java.util.concurrent.FutureTask.run(FutureTask.java) [na:1.8.0_271]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_271]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_271]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_271]
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.web.servlet.HandlerExceptionResolver]: Factory method 'handlerExceptionResolver' threw exception; nested exception is java.lang.IllegalStateException: Ambiguous @ExceptionHandler method mapped for [class org.springframework.web.bind.MethodArgumentNotValidException]: {public org.springframework.http.ResponseEntity com.datahome.activity.aop.ResultResponseBodyAdvice.handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException), public final org.springframework.http.ResponseEntity org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler.handleException(java.lang.Exception,org.springframework.web.context.request.WebRequest) throws java.lang.Exception}
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:185) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:650) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	... 30 common frames omitted
Caused by: java.lang.IllegalStateException: Ambiguous @ExceptionHandler method mapped for [class org.springframework.web.bind.MethodArgumentNotValidException]: {public org.springframework.http.ResponseEntity com.datahome.activity.aop.ResultResponseBodyAdvice.handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException), public final org.springframework.http.ResponseEntity org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler.handleException(java.lang.Exception,org.springframework.web.context.request.WebRequest) throws java.lang.Exception}
	at org.springframework.web.method.annotation.ExceptionHandlerMethodResolver.addExceptionMapping(ExceptionHandlerMethodResolver.java:101) ~[spring-web-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.web.method.annotation.ExceptionHandlerMethodResolver.<init>(ExceptionHandlerMethodResolver.java:65) ~[spring-web-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver.initExceptionHandlerAdviceCache(ExceptionHandlerExceptionResolver.java:281) ~[spring-webmvc-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver.afterPropertiesSet(ExceptionHandlerExceptionResolver.java:258) ~[spring-webmvc-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport.addDefaultHandlerExceptionResolvers(WebMvcConfigurationSupport.java:1007) ~[spring-webmvc-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport.handlerExceptionResolver(WebMvcConfigurationSupport.java:949) ~[spring-webmvc-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_271]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_271]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_271]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_271]
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:154) ~[spring-beans-5.2.8.RELEASE.jar:5.2.8.RELEASE]
	... 31 common frames omitted
```



## Secrity 的 `AccessDeniedHandler` 被 `@ExceptionHandler` 覆盖

https://stackoverflow.com/questions/50945698/difference-between-accessdeniedhandler-and-responseentityexceptionhandler
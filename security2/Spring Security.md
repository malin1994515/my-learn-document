# Spring Security 学习

学习目标

- 构建前后端分离的权限认证基础
- 多种方式并存的登录方式
- 多种匹配方式的并存
- 尝试与OAuth2.0进行整合



# 参考列表



spring security 官网

https://spring.io/projects/spring-security/#learn



江南一点雨

https://github.com/lenve/spring-security-samples



双斜杠少年

https://github.com/527515025/springBoot



codecraft(authorization code 模式使用postman验证)

https://segmentfault.com/a/1190000012275317



# Spring Security 架构

主要参考：https://spring.io/guides/topicals/spring-security-architecture



## Authentication and Access Control (认证和访问控制)	

> 应用程序的安全性可以总结为两个独立的问题
>
> - **authentication(`/ôˌTHen(t)iˈkāSH(ə)n/`)**认证(who are you?)：你是谁
> - **authorization(`/ˌôTHərəˈzāSH(ə)n/`)**授权(what are you allowed to do?)：你能做什么
>
> 有时候人们会说Access Control 而不是 Authorization ，这可能会造成混乱，但是以这种方式思考更容易理解。
>
> Spring Security的宗旨是将 Authentication 和 Authorization 区分开



### Authentication

> 相关设计模式：**委派模式**

Spring Security 关于认证最主要的接口就是 `AuthenticationManager`。

```java
public interface AuthenticationManager {
    Authentication authentication(Authentication authentication) 
        throw AuthenticationException;
}
```



`AuthenticationManager`最常用的实现是`ProviderManager`，它委派了`AuthenticationProvider`实例链。

`AuthenticationProvider`有点像`AuthenticationManager`，但是它有一个额外的方法，允许调用者查询它是否支持给定的`Authentication`类型。

```java
public interface AuthenticationProvider {
    Authentication authentication(Authentication authentication)
        throw AuthenticationException;
    
    boolean support(Class<?> authentication);
}
```

> `Class<?>`实际上是`Class<? extend Authentication>`仅用来询问它是否支持传递到`authentication()`方法中。如果不支持则跳过。



`ProviderManager`是具有可选的父级，如果所有的`AuthenticationProvider`都返回null，则可以询问父级。如果父级不可用，则null身份认证将导致`AuthenticationException`。



有时候应用程序会对资源进行逻辑分组(比如`/api/**`, `/resource/**`)使用不同的`ProviderManager`来处理，这个时候如果每个专属的`ProviderManager`有遗漏的地方，就可以使用父级的`ProviderManager`来进行兜底。

![ProviderManagers with a common parent](Spring Security.assets/authentication.png)



### Customizing Authentication Managers

Spring Security 提供了一些配置助手，可以快速的设置 AuthenticationManager。最常用的是`AuthenticationManagerBuilder`，它非常适合设置存储在 内存、JDBC、LDAP 的用户信息或自定义`UserDetailsService`。

```java
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
	// web stuff here
        
    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        builder.jdbcAuthentication().dataSource(dataSource)
            .withUser("dave").password("secret").roles("USER");
    }
}
```



> 如果我们在配置器中使用了@Override方法，那么`AuthenticationManagerBuilder`将仅用于构建'local' `AuthenticationManager`，它将是全局`AuthenticationManager`的子级。在Spring Boot Application中，可以`@Autowired`将全局bean连接到另一个bean中，但是不能使用 'local' bean，除非显式地公开它。



> 除非你通过提供自己的`AuthenticationManager`类型的Bean来抢占，否则Spring Boot提供了一个默认的全局`AuthenticationManager`(只有一个用户)。除非你主动需要自定义全局`AuthenticationManager`，否则默认的本身就足够安全，你不必担心太多。如果进行任何构建`AuthenticationManager`的配置，则通常可以在 'local' 对要保护的资源进行配置，而不必担心全局默认值。



> **<font color=red>请注意</font>**：上面的描述说明  AuthenticationManager为什么需要父级 AuthenticationManager进行了解释。并且告诉我们默认提供一个父级的AuthenticationManager。除非你自己手动声明一个全局 AuthenticationManager。并且通常情况下默认的父级足够安全。



### Authorization or Access Controller

> 相关设计模式：委派模式

Authentication successful后，我们可以继续进行 Authorization，这里的核心策略是`AccessDecisionManager`。框架提供了三种实现，所有这三种实现都委派给`AccessDecisionVoter`实例链，有点像`ProviderManager`委托给`AuthenticationProvider`实例链。



> decide 决定

```java
public interface AccessDecisionManager {
    void decide(Authentication authentication, Object object,
               Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
    		   InsufficientAuthenticationException;
    
    boolean supports(ConfigAttribute attribute);
    
    boolean supports（Class<?> clazz);
}
```



`AccessDecisionVoter`考虑了一个Authentication（代表主体）和一个secure object，该object已使用 `ConfigAttributes`装饰。

> Voter 投票人、选举人
>
> vote 投票

```java
public interface AccessDecisionVoter<S> {
    boolean supports(ConfigAttribute attribute);
    
    boolean supports(Class<?> clazz);
    
    int vote(Authentication authentication, S object,
            Collection<ConfigAttribute> attributes);
}
```

这个`Object`在`AccessDecisionManager`和`AccessDecisionVoter`的vote()中是完全通用的。它代表用户响应访问的任何内容（Web资源或Java类中的方法是两种最常见的情况）。`ConfigAttributes`也相当通用，用一些元数据来表示安全对象的修饰，该元数据确定访问它所需的权限级别。`ConfigAttributes`是一个接口。它只有一个方法(非常通用，并返回一个String)，因此这些字符串以某种方式编码资源所有者的意图，表达有关允许谁访问它的规则。典型的`ConfigAttribute`是用户角色的名称（如ROLE_ADMIN或ROLE_AUDIT），并且它们通常具有特殊的格式（如ROLE_前缀）或表示需要值的表达式。

> 这段话解释了vote()和decide()两个方法中3个参数
>
> - authentication 访问用户
> - object 被访问的资源
> - attributes 访问这个资源所需的条件
>
> 同时访问资源的条件必须具备某种特殊字符，如根据角色必须以ROLE_开头。（因为是根据特殊字符作为不同类型的区分）

大多数人都使用默认的`AccessDecisionManager`，它是`AffirmativeBased`（如果任何 voters 肯定地返回，则将授予访问权限）。通过添加新的活修改现有的工作方式，任何定制都倾向于在 voters中发生。

> 这句话告诉我们使用默认提供的`AccessDevisionManager`即可。
>
> 如果有多条规则限制一个资源，只要一条规则符合既可以访问该资源。(这个个人猜测需要验证)



使用Springn Expression Language(SpEL)的`ConfigAttribute`非常常见，例如`isFullyAuthenticated() && hashRole('user')`。`AccessDecisionVoter`支持此功能，它可以处理表达式并为其创建上下文。为了扩展可处理的表达式范围，需要`SecurityExpressionRoot`的自定义实现，有时还需要`SecurityExpressionHandler`。



## Web Security

​		Web层（用于UI和HTTP后端）中的Spring Security基于Servlet过滤器，因此通常首先了解一下过滤器的作用会很有帮助。下图显示了单个HTTP请求的处理程序的典型分层。

![Filter chain delegating to a Servlet](Spring Security.assets/filters.png)

​		客户端将请求发送到应用程序，然后容器根据请求url的路径确定对它使用哪些过滤器和哪个servlet。一个Servlet最多只能处理一个请求，但是过滤器形成一个链，因此它们是有序的。实际上，如果过滤器想要处理请求本身，则可以否决该链的其余部分。过滤器还可以修改下游过滤器和Servlet中使用的请求或响应。过滤器链的顺序非常重要，Spring Boot通过两种机制对其进行管理：类型为`Filter`的@Bean可以具有@Order或实现Ordered，它们可以是`FilterRegistrationBean`的一部分，而`FilterRegistrationBean`本身也具有顺序。一些现成的过滤器定义了自己的常量，以帮助表明它们相对彼此的顺序（例如，Spring session的SessionRepositoryFilter的DEFAULT_ORDER为Integer.MIN_VALUE+50，这告诉我们它喜欢处于链中的早期，但不排除其它过滤器在此之前）。

​		Spring Security是装载在链中的一个过滤器，其具体类型为`FilterChainPorxy`，出于我们稍后将介绍的原因。在Spring Boot Application中，Security Filter是ApplilcationContext中的@Bean，默认情况下已装载，以便将其应用于每个请求。它装载在 SecurityProperties.DEFAULT_FILTER_ORDER定义的位置，该位置又由FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER锚定（Spring Boot Application期望过滤器包装请求，以修改请求的行为的最大顺序）。但是，还有更多的功能：从容器角度来看，Spring Security是单个过滤器，但是在内部，还有其他过滤器，每个过滤器都扮演着特殊的角色。下图显示了这种关系。

![Spring Security Filter](Spring Security.assets/security-filters.png)

​		Spring Security是一个物理过滤器，但是将处理委托给一系列内部过滤器。

​		实际上，Security Filter 中甚至还有一层间接层：它通常以`DelegationFilterProxy`的形式装载在容器中，而不必是Spring @Bean。代理将其委托给`FilterChainProxy`，后者始终是@Bean，通常具有固定的 Spring Security Filter Chain名称。它是`FilterChainProxy`，它包含所有内部安全性逻辑，这些安全性逻辑在内部排列为一个或多个过滤器链。所有过滤器都具有相同的API（它们都实现了Servlet规范中的FIlter接口），并且它们都有机会否决该链的其余部分。

​		在同一顶级`FilterChainProxy`中，可以有多个由Spring Security管理的过滤器链，而对于容器而言，所有过滤器链都是未知的。Spring Security过滤器包含一个过滤器链列表，并向与其匹配的第一个链发送请求。下图显示了基于匹配请求路径（`/foo/**`在`/**`之前匹配）发生的调度。这是很常见的，但不是匹配请求的唯一方法。此调度过程的最重要特征是，只有一个链可以处理请求。

![Security Filter Dispatch](Spring Security.assets/security-filters-dispatch.png)

​		Spring Security `FilterChainProxy`将请求分发到匹配的第一个链。

​		没有自定义安全配置的普通spring boot application具有多个（称为n）过滤链，其中通常n=6。前（n-1）个链仅用于忽略静态资源模式，例如`/css/**`、`/images/**`，以及错误视图`/error`。（这些路径可以由用户使用SecurityProperties的security.ignored来控制）最后一个链匹配全部路径`/**`，并且更活跃，包含用于身份认证，授权，异常处理，会话的逻辑处理，header头修改等。默认情况下，该链中总共有11个过滤器，但是通常用户不必担心使用哪种过滤器以及何时使用呢。

> 容器不知道Spring Security内部的所有过滤器这一事实非常重要，尤其是在Spring Boot Application中，默认情况下，所有Filter类型的@Beans都会自动向容器注册。因此如果要向Security Chain中添加自定义过滤器，则无需使其成为@Bean或将其包装在`FilterRegistrationnBeann`中。

### Creating and Customizing Filter Chains

​		Spring Boot 应用程序（具有`/**`请求匹配器的应用程序）中的默认fallback filter chain具有SecurityProperties.BASIC_AUTH_ORDER的预定义顺序。你可以用过设置security.basic.enable=false完全关闭它，也可以将其用做 fallback filter chain并以较低的顺序定义其他规则。为此，添加一个类为`WebSecurityConfigurerAdapter`（或`WebSecurityConfigurer`）的@Bean，并使用@Order装饰该类

```java
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER-10)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/match1/**");
    }
}
```

​		这个bean使Spring Security添加一个新的过滤器链并在fallback之前对其进行排序。

​		许多应用程序对一组资源的访问规则与另一组完全不同。例如，承载UI和后台API的应用程序可能支持基于cookie的身份验证以及对UI部分的登录页面重定向，以及基于令牌的身份认证，其中包含对API部分的未经身份验证的请求401响应。每组资源都有其自己的`WebSecurityConfigurerAdapter`（具有唯一顺序）和自己的请求匹配器。如果匹配规则重叠，则最早的有序过滤器链将获胜。



### Request Matching for Dispatch and Authorization

​		一个Security filter chain（或等效的WebSecurityConfigurerAdapter）具有请求匹配器，该请求匹配器用于确定是否将其应用于HTTP请求。一旦决定应用特定的过滤器链，就不再应用其他过滤器链。但是，在过滤器链中，可以通过在`HttpSecurity`配置器中设置其他匹配来对授权进行更细粒度的控制。

```java
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER-10)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/match1/**")
            .authorizeRequests()
            	.antMatcher("/match1/user").hashRole("USER")
            	.antMatcher("/match1/spam").hashRole("SPAM")
            	.anyRequest().isAuthenticated();
    }
}
```

​		配置Spring Security时最容易犯的错误之一就是忘记了这些匹配器适用于不同的进程。一个是整个过滤器链的请求匹配器，另一个只是选择要应用的访问规则。

### Combining Application Security Rules with Actuator Rules

​		如果将Spring Boot Actuator用于管理端点，则可能希望它们是安全的，并且默认情况下是安全的。实际上，将执行器添加到Security Application后，你将获得仅适用于Actuator 端点的附件过滤器链。它由仅匹配Actuator端点的请求匹配器定义，并且具有ManagementServerProperties.BASIC_AUTH_ORDER的顺序，该顺序比默认的SecurityProperties fallback过滤器少5，因此在fallback之前先进行查询。

​		如果你希望将应用程序安全规则应用于Actuator端点，则可以添加一个过滤器链，该过滤器链的Order要早于Actuator加1，并且其请求匹配器包括所有执行器端点。如果你喜欢Actuator端点的默认安全性设置，那么最简单的方法是在Actuator端点之后但在fallback之前（例如ManagementServerProperties.BASIC_AUTH_ORDER+1）添加自己的过滤器。

```java
@Configuration
@Order(ManagermentServerProperties.BASIC_AUTH_ORDER+1)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void connfigure(HttpSecurity http) throws Exception {
        http.antMatcher("/foo/**");
    }
}
```

> Web层中的Spring Security当前与Servelet API绑定，因此它仅在以嵌入式或其他方式在servlet容器中运行应用程序时才真正使用哪个。但是，它与Spring MVC 或 Spring Web stack的其余部分无关，因此可以在任何servlet应用程序中使用，例如使用JAX-RS的servlet应用程序。



## Method Security

​		除了保护Web应用程序安全外，Spring Security还提供了将访问规则应用于Java方法的支持。对于Spring Security，这只是“protected resource”的另一种类型。对于用户来说，这意味着使用相同的`ConfigAttribute`字符串格式（例如：角色或表达式）声明访问规则。

```java
@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnable = true)
public class SampleSecurityApplication {
    
}
```

​		然后，我们可以直接装饰方法资源

```java
@Service
public class MyService {
    @Secured("ROLE_USER")
    public String secure() {
        return "Hello Security";
    }
}
```

​		此示例是具有安全性方法的服务。如果Spring创建了这种类型的@Bean，它将被代理，并且在实际执行该方法之前，调用者必须经过安全拦截器。如果访问被拒绝，则调用方法将获取`AccessDeniedException`而不是实际方法的结果。

​		你还可以在方法上使用其他注解来强制实施安全性约束，特别是`@PreAuthrize`和`@PostAuthorize`，它们可以让你编写分别包含对方法参数和返回值的引用的表达式。

> 结合Web安全性和方法安全性并不少见。过滤利器链提供用户体验功能，例如身份验证和重定向到登录页面等，并且方法安全性在更精细的级别上提供了保护。



## Working with Threads

​		Spring Security从根本上将是线程绑定的，因为它需要使当前经过身份验证的主体可供各种下游使用者使用。基本构件是`SecurityContext`，它可以包含一个`Authentication`（并且在用户登录时，他是一个经过显式`Authentication`的`authenticated`）。你始终可以通过`SecurityContextHolder`中的静态方法来访问和操纵SecurityContext，而后者又操纵`ThreadLocal`。以下示例显示了这种安排

```java
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
assert(authentication.isAuthenticated);
```

​		用户代码并不常见这样的操作，但是你需要编写自定义身份认证过滤器的情况下很有用（尽管如此，Spring Security中仍有一些基类可供使用，这样你就避免使用SecurityContextHolder）。

​		如果需要访问Web端点中当前已认证的用户，则可以在@RequestMapping中使用方法参数，如下所示

```java
@RequestMapping("/foo")
public String foo(@AuthenticationPrincipal User user) {
    // do stuff with user
}
```

​		该注解将当前的身份认证从`SecurityContext`中取出，并对其调用`getPrincipal()`方法以产生method参数。身份认证中主体的类取决于用于身份验证的`AuthenticationManager`，因此这对于获得对用哪个户数据的类型安全饮用是一个有用的小技巧。



### Processing Secure Methods Asynchronously

由于`SecurityContext`是线程绑定的，因此，如果要执行任何调用secure method方法的后台处理（例如使用@Async），则需要确保传播该上下文。归结为将`SecurityContext`于后台执行的任务(`Runnable`、`Callable`等)包装在一起。Spring Security提供了一些帮助程序，例如`Runnable`和`Callable`的包装器。要将`SecurityContext`传播到@Async方法，你需要提供`AsyncConfigurer`并确保`Executor`具有正确的类型

```java
@Configuration
public class ApplicationConfiguration extends AsyncConfigurerSupport {

  @Override
  public Executor getAsyncExecutor() {
    return new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(5));
  }

}
```


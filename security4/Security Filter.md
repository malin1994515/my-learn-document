# Security Filter



默认13个，通过开启日志



```yaml
logging:
  level:
    org.springframework.security: DEBUG
```



随意访问一个请求

日志都是从 FilterChainProxy 输出的，为了查看方便我把日志前一节删除了

```shell
/security/method/authorize/customSecurity at position 1 of 13 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
/security/method/authorize/customSecurity at position 2 of 13 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2021-04-01 09:41:37.367 DEBUG 21052 --- [nio-1000-exec-1] w.c.HttpSessionSecurityContextRepository : No HttpSession currently exists
2021-04-01 09:41:37.367 DEBUG 21052 --- [nio-1000-exec-1] w.c.HttpSessionSecurityContextRepository : No SecurityContext was available from the HttpSession: null. A new one will be created.
/security/method/authorize/customSecurity at position 3 of 13 in additional filter chain; firing Filter: 'HeaderWriterFilter'
/security/method/authorize/customSecurity at position 4 of 13 in additional filter chain; firing Filter: 'CsrfFilter'
/security/method/authorize/customSecurity at position 5 of 13 in additional filter chain; firing Filter: 'LogoutFilter'
2021-04-01 09:41:37.370 DEBUG 21052 --- [nio-1000-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /security/method/authorize/customSecurity' doesn't match 'POST /logout'
/security/method/authorize/customSecurity at position 6 of 13 in additional filter chain; firing Filter: 'UsernamePasswordAuthenticationFilter'
2021-04-01 09:41:37.371 DEBUG 21052 --- [nio-1000-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /security/method/authorize/customSecurity' doesn't match 'POST /login.do'
/security/method/authorize/customSecurity at position 7 of 13 in additional filter chain; firing Filter: 'ConcurrentSessionFilter'
/security/method/authorize/customSecurity at position 8 of 13 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2021-04-01 09:41:37.371 DEBUG 21052 --- [nio-1000-exec-1] o.s.s.w.s.HttpSessionRequestCache        : saved request doesn't match
/security/method/authorize/customSecurity at position 9 of 13 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
/security/method/authorize/customSecurity at position 10 of 13 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2021-04-01 09:41:37.372 DEBUG 21052 --- [nio-1000-exec-1] o.s.s.w.a.AnonymousAuthenticationFilter  : Populated SecurityContextHolder with anonymous token: 'org.springframework.security.authentication.AnonymousAuthenticationToken@26969961: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: null; Granted Authorities: ROLE_ANONYMOUS'
/security/method/authorize/customSecurity at position 11 of 13 in additional filter chain; firing Filter: 'SessionManagementFilter'
2021-04-01 09:41:37.372 DEBUG 21052 --- [nio-1000-exec-1] o.s.s.w.session.SessionManagementFilter  : Requested session ID 49d1010b-e717-4e20-863f-39aca5715097 is invalid.
/security/method/authorize/customSecurity at position 12 of 13 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
/security/method/authorize/customSecurity at position 13 of 13 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
```



经过删选的

```shell
/security/method at position 1 of 13 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
/security/method at position 2 of 13 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
/security/method at position 3 of 13 in additional filter chain; firing Filter: 'HeaderWriterFilter'
/security/method at position 4 of 13 in additional filter chain; firing Filter: 'CsrfFilter'
/security/method at position 5 of 13 in additional filter chain; firing Filter: 'LogoutFilter'
/security/method at position 6 of 13 in additional filter chain; firing Filter: 'UsernamePasswordAuthenticationFilter'
/security/method at position 7 of 13 in additional filter chain; firing Filter: 'ConcurrentSessionFilter'
/security/method at position 8 of 13 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
/security/method at position 9 of 13 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
/security/method at position 10 of 13 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
/security/method at position 11 of 13 in additional filter chain; firing Filter: 'SessionManagementFilter'
/security/method at position 12 of 13 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
/security/method at position 13 of 13 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
```





# 1. `WebAsyncManagerIntegrationFilter`

web异步管理集成过滤器

> 名字直译让人费解，还好源码并不复杂



```java
/**
 * Provides integration between the {@link SecurityContext} and Spring Web's
 * {@link WebAsyncManager} by using the
 * {@link SecurityContextCallableProcessingInterceptor#beforeConcurrentHandling(org.springframework.web.context.request.NativeWebRequest, Callable)}
 * to populate the {@link SecurityContext} on the {@link Callable}.
 *
 * @author Rob Winch
 * @see SecurityContextCallableProcessingInterceptor
 */
public final class WebAsyncManagerIntegrationFilter extends OncePerRequestFilter {
	private static final Object CALLABLE_INTERCEPTOR_KEY = new Object();

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        // 将WebAsyncManager 存储到Request中
        // 一个request是与一个 sessionId 绑定的
		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        // 将 SecurityContextCallableProcessingInterceptor 这个拦截器加入到 WebAsyncManager中
		SecurityContextCallableProcessingInterceptor securityProcessingInterceptor = (SecurityContextCallableProcessingInterceptor) asyncManager
				.getCallableInterceptor(CALLABLE_INTERCEPTOR_KEY);
		if (securityProcessingInterceptor == null) {
			asyncManager.registerCallableInterceptor(CALLABLE_INTERCEPTOR_KEY,
					new SecurityContextCallableProcessingInterceptor());
		}
		
        // 然后就溜溜球
		filterChain.doFilter(request, response);
	}
}
```



> `SecurityContextCallableProcessingInterceptor`  这个类不贴源码了，做的事情就是设置和清除 SecurityContext。



## `WebAsyncTask`  的关联

我当时第一时间看到这个类也很纳闷干嘛用的，直到发现 `WebAsyncTask` 是与 `WebAsyncManager` 有关系的。

> 用于在多线程环境的情况下获取用户上下文。

```java
/**
 * @description: web异步管理器测试
 * @author: malin
 * @create: 2021-04-01 10:20
 **/
@Slf4j
@RestController
@RequestMapping("/webAsyncManager")
public class WebAsyncManagerTestController {

    @GetMapping("/webAsyncTask")
    public WebAsyncTask getContextByWebAsyncTask() {
        log.info("1: main线程开始[{}]", Thread.currentThread().getName());

        WebAsyncTask<SecurityUser> task = new WebAsyncTask<>(300L, () -> {
            log.info("1: WebAsyncTask异步线程[{}]，开始执行任务", Thread.currentThread().getName());
            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("2: WebAsyncTask异步线程[{}]，任务执行完成，结果：{}", Thread.currentThread().getName(), JSONUtil.toJsonPrettyStr(user));
            return user;
        });

        log.info("2: main线程结束[{}]", Thread.currentThread().getName());
        return task;
    }

    @GetMapping("/runnable")
    public SecurityUser getContextByRunnable() throws Exception {
        log.info("1: main线程开始[{}]", Thread.currentThread().getName());

        final List<SecurityUser> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        Thread async = new Thread(() -> {
            try {
                log.info("1: Runnable异步线程[{}]，开始执行任务", Thread.currentThread().getName());
                SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                list.add(user);
            } finally {
                latch.countDown();
            }
        });

        async.start();
        latch.await();
        log.info("2: main线程结束[{}]", Thread.currentThread().getName());
        return list.isEmpty() ? null : list.get(0);

    }
}
```



# 2. `SecurityContextPersistenceFilter`

安全上下文持久化Filter



- 请求开始时从对应的`SecurityContextRepository`获取`SecurityContext`存入`SecurityContextHolder`
- 请求结束时清除`SecurityContextHolder`中的`SecurityContext`，将本次请求执行后新的`SecurityContext`存入对应的`SecurityContextRepository`中



关键代码片段

```java
HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

try {
    SecurityContextHolder.setContext(contextBeforeChainExecution);

    chain.doFilter(holder.getRequest(), holder.getResponse());

}
finally {
    // 为什么这里是请求执行后？
    // 因为 chain.doFilter() 是一连串不停的入方法栈，因为栈的先入后出，所以这块finally是轮到
    // SecurityContextPersistenceFilter 后面一连串的 chain.doFilter() 执行完之后执行
    SecurityContext contextAfterChainExecution = SecurityContextHolder
        .getContext();
    // Crucial removal of SecurityContextHolder contents - do this before anything
    // else.
    SecurityContextHolder.clearContext();
    repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
    request.removeAttribute(FILTER_APPLIED);

    if (debug) {
        logger.debug("SecurityContextHolder now cleared, as request processing completed");
    }
}
```



> `WebAsyncManagerIntegrationFilter` 使用 `SecurityContextCallableProcessingInterceptor` 来在请求前后做事情。
>
> 但是 `SecurityContextPersistenceFilter` 利用try finally 和方法栈达到在请求前后做事情。  



# 3. `HeaderWriterFilter`

Header写入Filter



- 将浏览器需要的一些头信息写入 response



# 4. `CsrfFilter`

CSRF(Cross-site Request forgery) 跨站请求伪造的处理

`CsrfFilter` 做的事情就是从request的header 或者 from 中取出 csrf_token 然后与 csrfTokenRepository中存储的进行比较。避免跨站请求伪造。



# 5. `LogoutFilter`

登出Filter

- 通过`RequestMatcher` 来匹配当前uri
- 如果请求的登出，那么调用一组  `LogoutHandler` 和 一个`LogoutSuccessHandler` 进行登出后的处理



# 6. `UsernamePasswordAuthenticationFilter`

账号密码认证Filter

> 这是个老朋友了，大部分学会Security简单配置后，基本上第一个接触到的就是这个Filter。以及后续开发通过 手机验证码 等登陆时都需要参考这个Filter。
>
> 从名字上就能知道是通过账号密码的方法进行认证的。



但是这个类里面只有实现。具体的逻辑在抽象基类`AbstractAuthenticationProcessingFilter`的 `doFilter()`中。

- 通过`RequestMatcher` 来匹配当前url。
- 而 `AbstractAuthenticationProcessingFilter` 又是通过 `AuthenticationManager` 来进行实际的认证操作。



仔细分析 `doFilter` 方法。这里一定要看注释(我猜是框架作者认为这个方法非常重要，所有加了特别多注释)里面包含了很多有用的信息。

- 调用 `requiresAuthentication(HttpServletRequest, HttpServletResponse)` 方法来确定请求是否用于身份验证，是由应该由此过滤器处理。如果是身份身份验证请求，则调用 `attemptAuthentication(HttpServletRequest, HttpServletResponse)`来执行身份验证。然后有三种可能的结果
  - 返回一个 `Authentication` 对象。已配置的`SessionAuthenticationStrategy` 将被(以处理任何与会话相关的行为，例如创建新会话以防止会话固定攻击)，然后调用`successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)`方法。
  - 身份认证期间发生`AuthenticationException`这将调用`unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException)` 方法。
  - 返回Null，表示认证过程不完整。方法将立即返回，假设子类已完成任何操作继续认证过程所需的工作(例如重定向)。这假设次方法将接收到以后的请求，其中返回的`Authentication`对象不为Null。

```java
/**
* Invokes the
* {@link #requiresAuthentication(HttpServletRequest, HttpServletResponse)
* requiresAuthentication} method to determine whether the request is for
* authentication and should be handled by this filter. If it is an authentication
* request, the
* {@link #attemptAuthentication(HttpServletRequest, HttpServletResponse)
* attemptAuthentication} will be invoked to perform the authentication. There are
* then three possible outcomes:
* <ol>
* <li>An <tt>Authentication</tt> object is returned. The configured
* {@link SessionAuthenticationStrategy} will be invoked (to handle any
* session-related behaviour such as creating a new session to protect against
* session-fixation attacks) followed by the invocation of
* {@link #successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
* method</li>
* <li>An <tt>AuthenticationException</tt> occurs during authentication. The
* {@link #unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException)
* unsuccessfulAuthentication} method will be invoked</li>
* <li>Null is returned, indicating that the authentication process is incomplete. The
* method will then return immediately, assuming that the subclass has done any
* necessary work (such as redirects) to continue the authentication process. The
* assumption is that a later request will be received by this method where the
* returned <tt>Authentication</tt> object is not null.
* </ol>
*/
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
	
    // 1. 先判断这个请求是否是身份验证请求
    if (!requiresAuthentication(request, response)) {
        chain.doFilter(request, response);

        return;
    }

    if (logger.isDebugEnabled()) {
        logger.debug("Request is to process authentication");
    }

    Authentication authResult;

    try {
        // 2. 进行身份验证
        authResult = attemptAuthentication(request, response);
        
        // 2.3 返回Null，表示认证过程不完整。
        // 方法将立即返回，假设子类已完成任何操作继续认证过程所需的工作(例如重定向)。
        // 这假设次方法将接收到以后的请求，其中返回的`Authentication`对象不为Null。
        if (authResult == null) {
            // return immediately as subclass has indicated that it hasn't completed
            // authentication
            return;
        }
        // 2.1 身份验证成功调用SessionAuthenticationStrategy的onAuthentication处理任何与会话相关的行为
        sessionStrategy.onAuthentication(authResult, request, response);
    }
    
    // 2.2 身份验证出现问题调用 unsuccessfulAuthentication 进行验证失败后的处理
    catch (InternalAuthenticationServiceException failed) {
        logger.error(
            "An internal error occurred while trying to authenticate the user.",
            failed);
        unsuccessfulAuthentication(request, response, failed);

        return;
    }
    catch (AuthenticationException failed) {
        // Authentication failed
        unsuccessfulAuthentication(request, response, failed);

        return;
    }

    // Authentication success
    if (continueChainBeforeSuccessfulAuthentication) {
        chain.doFilter(request, response);
    }
	
    // 2.1 调用successfulAuthentication 来进行验证成功后的处理
    successfulAuthentication(request, response, chain, authResult);
}
```



## `attemptAuthentication(HttpServletRequest, HttpServletResponse)`



```java
public Authentication attemptAuthentication(HttpServletRequest request,
                                            HttpServletResponse response) throws AuthenticationException {
    // http method 必须是 POST
    if (postOnly && !request.getMethod().equals("POST")) {
        throw new AuthenticationServiceException(
            "Authentication method not supported: " + request.getMethod());
    }
	
    // 通过 request.getParameter(String) 从请求中提取出账号&密码
    String username = obtainUsername(request);
    String password = obtainPassword(request);

    if (username == null) {
        username = "";
    }

    if (password == null) {
        password = "";
    }

    username = username.trim();
	
    // 构建需要认证Token  (如果自己拓展手机号+验证码登陆也需要写一个)
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        username, password);

    // Allow subclasses to set the "details" property
    setDetails(request, authRequest);

    // 使用 AuthenticationManager 进行身份认证
    // UsernamePasswordAuthenticationToken 是 Authentication 的子类
    // AuthenticationManager
    return this.getAuthenticationManager().authenticate(authRequest);
}
```





## `ProviderManager#authenticate(Authentication)`

最终使用的是在`SecurityConfig`配置的`DaoAuthenticationProvider` 来进行实际的身份验证。



- `DaoAuthenticationProvider`   -->  `AbstractUserDetailsAuthenticationProvider`



```java
public Authentication authenticate(Authentication authentication)
    throws AuthenticationException {
    Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                        () -> messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.onlySupports",
                            "Only UsernamePasswordAuthenticationToken is supported"));

    // Determine username
    String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
        : authentication.getName();

    boolean cacheWasUsed = true;
    // 从缓存中查询用户(针对用户频繁登陆导致每次都需要去查询关系型数据库)
    UserDetails user = this.userCache.getUserFromCache(username);

    if (user == null) {
        cacheWasUsed = false;

        try {
            // 检索用户这里是关键
            // 这里是关键  DaoAuthenticationProvider#retrieveUser(String, UsernamePasswordAuthenticationToken)
            // 调用了DaoAuthenticationProvider 配置的 UserDetailsService (这就是在SecurityConfig中注入的自定义UserDetailsService)
            user = retrieveUser(username,
                                (UsernamePasswordAuthenticationToken) authentication);
        }
        catch (UsernameNotFoundException notFound) {
            logger.debug("User '" + username + "' not found");

            if (hideUserNotFoundExceptions) {
                throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
            }
            else {
                throw notFound;
            }
        }

        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
    }

    try {
        // 前置检查 锁定、启用、过期
        preAuthenticationChecks.check(user);
        // 额外检查 凭证、密码
        additionalAuthenticationChecks(user,
                                       (UsernamePasswordAuthenticationToken) authentication);
    }
    catch (AuthenticationException exception) {
        if (cacheWasUsed) {
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser(username,
                                (UsernamePasswordAuthenticationToken) authentication);
            preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user,
                                           (UsernamePasswordAuthenticationToken) authentication);
        }
        else {
            throw exception;
        }
    }
	
    // 后置检查 认证有没有过期
    postAuthenticationChecks.check(user);

    // 加入缓存
    if (!cacheWasUsed) {
        this.userCache.putUserInCache(user);
    }

    Object principalToReturn = user;

    if (forcePrincipalAsString) {
        principalToReturn = user.getUsername();
    }
	
    // 重新创建一个 UsernamePasswordAuthenticationToken 返回
    return createSuccessAuthentication(principalToReturn, authentication, user);
}
```



# 7. `ConcurrentSessionFilter`

并发session Filter



主要是和session打交道的一个过滤器

- 代码一目了然，获取session 如果过期就跳转到logout，否则就根据sessionId刷新最后请求时间

```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    HttpSession session = request.getSession(false);

    if (session != null) {
        // 请注意这一行，sessionRegistry获取
        SessionInformation info = sessionRegistry.getSessionInformation(session.getId());

        if (info != null) {
            if (info.isExpired()) {
                // Expired - abort processing
                if (logger.isDebugEnabled()) {
                    logger.debug("Requested session ID "
                                 + request.getRequestedSessionId() + " has expired.");
                }
                doLogout(request, response);

                this.sessionInformationExpiredStrategy.onExpiredSessionDetected(new SessionInformationExpiredEvent(info, request, response));
                return;
            }
            else {
                // Non-expired - update last request date/time
                sessionRegistry.refreshLastRequest(info.getSessionId());
            }
        }
    }

    chain.doFilter(request, response);
}
```



## 关于 `SessionRegistory` 其他用处

这个类是用来做session注册的，默认是存在JVM内存中，既然可以在JVM内存中那应该就是可以存储在别的介质中。Redis(推荐)、Mysql(当然这个是不推荐的)、Mongodb。

> 为什么存储在Redis中要比JVM内存中好？
>
> 因为两个JVM进程(应用启动两个副本)的内存是不能共享的。



使用自定义的`SessionRegistory`替换默认

```java
@Bean
public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
}

http.sessionManagement()
    .maximumSessions(1)
    .sessionRegistry(sessionRegistry());
```



使用`SessionRegistory` 获取当前在线的所有用户

```java
@Resource
private SessionRegistry sessionRegistry;

@GetMapping("/onlineUser")
public List<Object> onlineUser() {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
        .map(Object::toString)
        .collect(Collectors.toList()) ;
}
```



# 8. `RequestCacheAwareFilter` 

请求缓存感知Filter



> 看注释描述大概是：如果当前请求与`RequestCache`匹配。那么将缓存的请求传递下去。
>
> **所以重点问题就是 `RequestCache` 的匹配规则是什么？**

```java
/**
 * Responsible for reconstituting the saved request if one is cached and it matches the
 * current request.
 * <p>
 * It will call
 * {@link RequestCache#getMatchingRequest(HttpServletRequest, HttpServletResponse)
 * getMatchingRequest} on the configured <tt>RequestCache</tt>. If the method returns a
 * value (a wrapper of the saved request), it will pass this to the filter chain's
 * <tt>doFilter</tt> method. If null is returned by the cache, the original request is
 * used and the filter has no effect.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class RequestCacheAwareFilter extends GenericFilterBean {

	private RequestCache requestCache;

	public RequestCacheAwareFilter() {
		this(new HttpSessionRequestCache());
	}

	public RequestCacheAwareFilter(RequestCache requestCache) {
		Assert.notNull(requestCache, "requestCache cannot be null");
		this.requestCache = requestCache;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest wrappedSavedRequest = requestCache.getMatchingRequest(
				(HttpServletRequest) request, (HttpServletResponse) response);

		chain.doFilter(wrappedSavedRequest == null ? request : wrappedSavedRequest,
				response);
	}

}
```



### `RequestCache` 匹配规则

排查 `public RequestCacheAwareFilter(RequestCache)` 被调用的地方。



```java
@Override
public void configure(H http) {
    // 2. 让我们看看 RequestCache 的具体实例
    RequestCache requestCache = getRequestCache(http);
    // 1. 这里实例化的 RequestCacheAwareFilter
    RequestCacheAwareFilter requestCacheFilter = new RequestCacheAwareFilter(
        requestCache);
    requestCacheFilter = postProcess(requestCacheFilter);
    http.addFilter(requestCacheFilter);
}

// 如果没有共享的 RequestCache 就创建一个默认的 HttpSessionRequestCache
/**
* Gets the {@link RequestCache} to use. If one is defined using
* {@link #requestCache(org.springframework.security.web.savedrequest.RequestCache)},
* then it is used. Otherwise, an attempt to find a {@link RequestCache} shared object
* is made. If that fails, an {@link HttpSessionRequestCache} is used
*
* @param http the {@link HttpSecurity} to attempt to fined the shared object
* @return the {@link RequestCache} to use
*/
private RequestCache getRequestCache(H http) {
    RequestCache result = http.getSharedObject(RequestCache.class);
    if (result != null) {
        return result;
    }
    result = getBeanOrNull(RequestCache.class);
    if (result != null) {
        return result;
    }
    HttpSessionRequestCache defaultCache = new HttpSessionRequestCache();
    defaultCache.setRequestMatcher(createDefaultSavedRequestMatcher(http));
    return defaultCache;
}

// 当然我们没有共享过 RequestChache，所以可以直接看 HttpSessionRequestCache 的配置
private RequestMatcher createDefaultSavedRequestMatcher(H http) {
    RequestMatcher notFavIcon = new NegatedRequestMatcher(new AntPathRequestMatcher("/**/favicon.*"));

    RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
        new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

    boolean isCsrfEnabled = http.getConfigurer(CsrfConfigurer.class) != null;

    List<RequestMatcher> matchers = new ArrayList<>();
    if (isCsrfEnabled) {
        RequestMatcher getRequests = new AntPathRequestMatcher("/**", "GET");
        matchers.add(0, getRequests);
    }
    matchers.add(notFavIcon);
    matchers.add(notMatchingMediaType(http, MediaType.APPLICATION_JSON));
    matchers.add(notXRequestedWith);
    matchers.add(notMatchingMediaType(http, MediaType.MULTIPART_FORM_DATA));
    matchers.add(notMatchingMediaType(http, MediaType.TEXT_EVENT_STREAM));

    return new AndRequestMatcher(matchers);
}

```



大概总结一下那些请求会被缓存

> 如果没看源码网上大把的博客直接就是   **必须是GET请求**
>
> 但是实际上有的是项目关闭了CSRF  `http.csrf().disable()`

- 非favicon图标
- 头信息里不包含 `X-Requested-With` 或 `XMLHttpRequest` 的
- 如果开启了 CSRF 那么只会缓存 GET 请求  
- 请求头不包含 `application/json`
- 请求头不包含 `multipart/form-data`
- 请求头不包含 `text/event-stream`



## 如何让`RequestCacheAwareFilter`失效？

> 前面源码部分已经了解会从共享的RequestCache中获取。

```java
http.requestCache().requestCache(new NullRequestCache());
```



# 9. `SecurityContextHolderAwareRequestFilter`

这个Filter针对 Request进行了Wrapper。然后重新传递下去。



```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    chain.doFilter(this.requestFactory.create((HttpServletRequest) req, (HttpServletResponse) res), res);
}
```



## `HttpServletRequestFactory`

正是由这个工厂类创建的Wrapper类

```java
@Override
public HttpServletRequest create(HttpServletRequest request,
                                 HttpServletResponse response) {
    return new Servlet3SecurityContextHolderAwareRequestWrapper(request, this.rolePrefix, response);
}

// 包装的这个类里面功能是真的多， 登录、退出、判断是否登录
private class Servlet3SecurityContextHolderAwareRequestWrapper
    extends SecurityContextHolderAwareRequestWrapper {
    private final HttpServletResponse response;

    Servlet3SecurityContextHolderAwareRequestWrapper(
        HttpServletRequest request, String rolePrefix,
        HttpServletResponse response) {
        super(request, HttpServlet3RequestFactory.this.trustResolver, rolePrefix);
        this.response = response;
    }

    @Override
    public AsyncContext getAsyncContext() {
        AsyncContext asyncContext = super.getAsyncContext();
        if (asyncContext == null) {
            return null;
        }
        return new SecurityContextAsyncContext(asyncContext);
    }

    @Override
    public AsyncContext startAsync() {
        AsyncContext startAsync = super.startAsync();
        return new SecurityContextAsyncContext(startAsync);
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest,
                                   ServletResponse servletResponse) throws IllegalStateException {
        AsyncContext startAsync = super.startAsync(servletRequest, servletResponse);
        return new SecurityContextAsyncContext(startAsync);
    }

    @Override
    public boolean authenticate(HttpServletResponse response)
        throws IOException, ServletException {
        AuthenticationEntryPoint entryPoint = HttpServlet3RequestFactory.this.authenticationEntryPoint;
        if (entryPoint == null) {
            HttpServlet3RequestFactory.this.logger.debug(
                "authenticationEntryPoint is null, so allowing original HttpServletRequest to handle authenticate");
            return super.authenticate(response);
        }
        if (isAuthenticated()) {
            return true;
        }
        entryPoint.commence(this, response,
                            new AuthenticationCredentialsNotFoundException(
                                "User is not Authenticated"));
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        if (isAuthenticated()) {
            throw new ServletException("Cannot perform login for '" + username
                                       + "' already authenticated as '" + getRemoteUser() + "'");
        }
        AuthenticationManager authManager = HttpServlet3RequestFactory.this.authenticationManager;
        if (authManager == null) {
            HttpServlet3RequestFactory.this.logger.debug(
                "authenticationManager is null, so allowing original HttpServletRequest to handle login");
            super.login(username, password);
            return;
        }
        Authentication authentication;
        try {
            authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (AuthenticationException loginFailed) {
            SecurityContextHolder.clearContext();
            throw new ServletException(loginFailed.getMessage(), loginFailed);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void logout() throws ServletException {
        LogoutHandler handler = HttpServlet3RequestFactory.this.logoutHandler;
        if (handler == null) {
            HttpServlet3RequestFactory.this.logger.debug(
                "logoutHandlers is null, so allowing original HttpServletRequest to handle logout");
            super.logout();
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        handler.logout(this, this.response, authentication);
    }

    private boolean isAuthenticated() {
        Principal userPrincipal = getUserPrincipal();
        return userPrincipal != null;
    }
}
```



## 和`ResponseBodyAdvice<T>`的联动

> 因为过滤链的关系，如果使用了security。那么 `beforeBodyWrite` 方法里面也可以进行判断是否登录，登出的操作。
>
> 虽然没什么用但是的确可以这么做。

```java
/**
 * @description: Controller 返回体封装
 * @author: malin
 * @create: 2021-03-29 09:49
 **/
@Slf4j
@RestControllerAdvice(basePackages = "com.datahome.basic.controller")
public class ResultResponseAdvice implements ResponseBodyAdvice<Object> {

    // 如果被 @ResultModelIgnore 注解标注则忽略
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.hasMethodAnnotation(ResultModelIgnore.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 返回的 MediaType 为 application/json 则进行一次封装，否则直接返回不做处理
        return selectedContentType.equals(MediaType.APPLICATION_JSON) ? ResultModel.okData(body) : body;
    }
}
```



# 10. `AnonymousAuthenticationFilter`

匿名认证Filter

> 看名字我已经猜到，如果你是一个没有经过认证的用户，那么你访问任何接口都是以匿名用户的角色。



代码上也很简单，如果走到这一步上下文还没有认证信息，那么久创建一个匿名用户加入上下文。

```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
        SecurityContextHolder.getContext().setAuthentication(
            createAuthentication((HttpServletRequest) req));

        if (logger.isDebugEnabled()) {
            logger.debug("Populated SecurityContextHolder with anonymous token: '"
                         + SecurityContextHolder.getContext().getAuthentication() + "'");
        }
    }
    else {
        if (logger.isDebugEnabled()) {
            logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
                         + SecurityContextHolder.getContext().getAuthentication() + "'");
        }
    }

    chain.doFilter(req, res);
}

protected Authentication createAuthentication(HttpServletRequest request) {
    AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(key, principal, authorities);
    auth.setDetails(authenticationDetailsSource.buildDetails(request));

    return auth;
}
```



# 11. `SessionManagementFilter`

会话管理Filter

> 检测自请求以来已对用户进行身份验证，如果存在，则调用已配置的`SessionAuthenticationStrategy`以执行任何与Session相关的活动。
>
> 例如：激活会话固定攻击或检查多个并发登录。



```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
	
    // 防止并发登录
    if (request.getAttribute(FILTER_APPLIED) != null) {
        chain.doFilter(request, response);
        return;
    }

    request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

    // 请特别注意，这里和 2. SecurityContextPersistenceFilter 是有关联的，如果一个请求是第一次过来会进入到这个 if 代码块
    // 然后在进入  SecurityContextPersistenceFilter 的 finally 代码块进行持久化
    // 如果重复请求就不会进入这个 if 代码块
    if (!securityContextRepository.containsContext(request)) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !trustResolver.isAnonymous(authentication)) {
            // The user has been authenticated during the current request, so call the
            // session strategy
            try {
                sessionAuthenticationStrategy.onAuthentication(authentication,  request, response);
            }
            catch (SessionAuthenticationException e) {
                // The session strategy can reject the authentication
                logger.debug("SessionAuthenticationStrategy rejected the authentication object", e);
                SecurityContextHolder.clearContext();
                failureHandler.onAuthenticationFailure(request, response, e);

                return;
            }
            // Eagerly save the security context to make it available for any possible
            // re-entrant
            // requests which may occur before the current request completes.
            // SEC-1396.
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
        }
        else {
            // No security context or authentication present. Check for a session
            // timeout
            if (request.getRequestedSessionId() != null
                && !request.isRequestedSessionIdValid()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Requested session ID " + request.getRequestedSessionId() + " is invalid.");
                }

                if (invalidSessionStrategy != null) {
                    invalidSessionStrategy.onInvalidSessionDetected(request, response);
                    return;
                }
            }
        }
    }

    chain.doFilter(request, response);
}
```



# 12. `ExceptionTranslationFilter`

异常转换Filter

> 前面的filter，只有 `UsernamePasswordAuthentication` 里面有 try catch  会在抛异常后使用unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, Exception) 直接进行处理。其他Filter并没有对异常进行处理。



```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    try {
        chain.doFilter(request, response);

        logger.debug("Chain processed normally");
    }
    // 将IO异常直接上抛
    catch (IOException ex) {
        throw ex;
    }
    catch (Exception ex) {
        // 尝试从栈中提取出  SpringSecurityException
        // Try to extract a SpringSecurityException from the stacktrace
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
        RuntimeException ase = (AuthenticationException) throwableAnalyzer
            .getFirstThrowableOfType(AuthenticationException.class, causeChain);

        if (ase == null) {
            ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(
                AccessDeniedException.class, causeChain);
        }

        // 处理SpringSecurityException
        if (ase != null) {
            if (response.isCommitted()) {
                throw new ServletException("Unable to handle the Spring Security Exception because the response is already committed.", ex);
            }
            handleSpringSecurityException(request, response, chain, ase);
        }
        else {
            // 原样抛出 ServletException 和 RuntimeException
            // Rethrow ServletExceptions and RuntimeExceptions as-is
            if (ex instanceof ServletException) {
                throw (ServletException) ex;
            }
            else if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }

            // 这里应该是永远也不会走到，因为前面已经考虑到所有情况
            // Wrap other Exceptions. This shouldn't actually happen
            // as we've already covered all the possibilities for doFilter
            throw new RuntimeException(ex);
        }
    }
}
```





# 13. `FilterSecurityInterceptor`

这个类命名真是让人迷惑，虽然叫拦截器实际上既是一个拦截器也是一个过滤器。

```java
public class FilterSecurityInterceptor extends AbstractSecurityInterceptor implements
		Filter {
    ...
}
```



```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    FilterInvocation fi = new FilterInvocation(request, response, chain);
    invoke(fi);
}


public void invoke(FilterInvocation fi) throws IOException, ServletException {
    // 重复请求
    if ((fi.getRequest() != null)
        && (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
        && observeOncePerRequest) {
        // filter already applied to this request and user wants us to observe
        // once-per-request handling, so don't re-do security checking
        fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
    }
    else {
        // first time this request being called, so perform security checking
        if (fi.getRequest() != null && observeOncePerRequest) {
            fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
        }
		
        // 请注意这一行的实现才是关键，这里面获取配置然后判断是否能够 access
        InterceptorStatusToken token = super.beforeInvocation(fi);

        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }
        finally {
            super.finallyInvocation(token);
        }

        super.afterInvocation(token, null);
    }
}
```





# 总结

个人总结



最重要

- UsernamePasswordAuthenticationFilter 这个管理 Authentication(认证)
- FilterSecurityInterceptor 这个处理Authorization(授权- 或者叫Access Controller更合适)



次一级

- ExceptionTranslationFilter 处理异常
- SessionManagementFilter 和 SecurityContextPersistenceFilter 。  这里有点意思虽然 SecurityContextPersistenceFilter 在前面但是因为 方法栈的关系，它的持久化操作反而是在 SessionManagermentFilter 之后执行的。



其他

不是说不重要，大部分都是安全防御类的需要的用的配置一下







# 未完成列表

- SecurityContextRepository
- SessionAuthenticationStrategy
- UsernamePasswordAuthenticationFilter 
- FilterSecurityInterceptor  -- 这个从源码已知的细节来看是通过  url + role 也就是 HttpSecurity中配置的Access Controller。
  - MethodSecurityInterceptor 这是一个同级的实现类 个人猜想是处理  @Secured @PreAuthorize 等 Method Security 。
  - AbstractSecurityInterceptor 以上两个类的大部分逻辑都是在这个抽象基类中完成的。所以这个类的源码有点复杂一时半会看不明白
- RequestMatcher



# 杂项

## `SecurityProperties`

Security 配置



## `UserDetailsServiceAutoConfiguration`

用户详细服务自动配置




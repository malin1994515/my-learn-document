# OAuth 2.0

- http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html
  - 理解OAuth 2.0

- http://www.ruanyifeng.com/blog/2019/04/oauth_design.html
  - 用类比的方式介绍了OAuth 2.0
- http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html
  - OAuth 2.0 的四种方式



# 参考实现

- http://blog.didispace.com/spring-security-oauth2-xjf-1/
  - 也能启动起来，但是登录却一直是 401
- https://www.cnblogs.com/cjsblog/p/10548022.html
  - 前后端都有的DEMO
- https://blog.csdn.net/u012702547/article/details/105699777
  - 这个最全 并且github上有多种实现方式(最终参考这个Demo实现)
    - https://github.com/lenve/oauth2-samples



# 为什么要OAuth 2.0?

# 什么是OAuth 2.0

​		OAuth引入了一个授权层，用来分离两种不同的角色：客户端和资源所有者。

​		资源所有者同意以后，资源服务器可以向客户端办法**令牌**。**客户端**通过**令牌**，去请求数据。

> 这段话的意思就是，**OAuth的核心就是向第三方应用颁发令牌**。

​		由于互联网有多种场景，RFC 6749定义了获得令牌的四种授权方式(authorization grant)。

​		也就是说，**OAuth2.0规定了四种获得令牌的流程。你可以选择最合适自己的那一种，向第三方应用颁发令牌**。下面就是这四种授权方式。

> - 授权码(authorization code)
> - 隐藏式(implicit)
> - 密码式(password)
> - 客户端凭证(client credentials)
>
> 注意，不管哪一种授权方式，第三方应用申请令牌之前，都必须先到系统备案，说明自己的身份，然后会拿到两个身份识别码：客户端ID(client ID)和客户端密钥(client secret)。这是为了防止令牌被滥用，没有备案过第三方应用，是不会拿到令牌的。

## OAuth 2.0 四种模式



### 授权码(authorization code)

​		授权码(authorization code)方式，指的是第三方应用先申请一个授权码，然后再用改码获取令牌。



### 简化(implicit)

### 密码(password)

### 客户端(client credentials)
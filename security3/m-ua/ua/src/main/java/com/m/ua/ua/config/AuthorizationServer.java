package com.m.ua.ua.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    // 用户配置客户端
    // ClientDetailsServiceConfigurer 提供 memory 和 jdbc
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .secret("123")
                .redirectUris("http://127.0.0.1:1000/autologin")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("all")
        .and()
                .withClient("client2")
                .secret("123")
                .redirectUris("http://127.0.0.1:2000/autologin")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("all");
    }

    // AuthorizationServerEndpointsConfigurer 配置器 关于授权(authorization)所有用到的Bean(默认提供了一套default)
    // AuthorizationServerEndpointsConfiguration 配置 使用AuthorizationServerEndpointsConfigurer中的Bean，例如处理 /oauth/authorization/ 和 /oauth/token 的 endpoints(类似Controller、Servlet)
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 先使用default，过一会使用自定义的比如 tokenServices、authorizationCodeService 等
        super.configure(endpoints);
    }
}

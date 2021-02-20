package com.malin.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class UsernamePasswordSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private DataSource dataSource;


    // 使用内存存储认证数据
    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        User user = new User("admin", "123456", new ArrayList<>());
        auth.inMemoryAuthentication()
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser(user);
    }*/

    // 使用数据库存储认证数据
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}

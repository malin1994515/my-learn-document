package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity // 使用Spring Boot时已经自动装配过，个人喜好显示的写出来。
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    // 增加角色层级(admin 拥有 user 的全部权限)
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("123").roles("admin")
                .and()
                .withUser("malin").password("123").roles("user");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                // 登录页面、登录请求地址
                .and().formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll()
                // 登录成功的处理器(这个处理器是返回json)
                .successHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");

                    PrintWriter out = resp.getWriter();
                    try {
                        out.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal()));
                    } finally {
                        out.flush();
                        out.close();
                    }
                })
                // 登录失败的处理器
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());

                    PrintWriter out = resp.getWriter();
                    try {
                        out.write(e.getMessage());
                    } finally {
                        out.flush();
                        out.close();
                    }
                })
                // 异常处理器（未登录的情况）
                .and().exceptionHandling().authenticationEntryPoint((req, resp, authException) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    PrintWriter out = resp.getWriter();
                    try {
                        out.write("尚未登录，请先登录");
                    } finally {
                        out.flush();
                        out.close();
                    }
                })
                // 注销接口
                .and().logout().logoutUrl("/logout").logoutSuccessHandler((req,resp,authentication)->{
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    try {
                        out.write("注销成功");
                    } finally {
                        out.flush();
                        out.close();
                    }
                })
                .and().csrf().disable();
    }
}

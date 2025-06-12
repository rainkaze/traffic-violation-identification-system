package edu.cupk.trafficviolationidentificationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/hello").permitAll() // 1. 对/hello接口的请求，允许所有用户访问
                                .anyRequest().authenticated()           // 2. 其他所有请求，都需要进行身份认证
                )
                // 暂时禁用 CSRF 防护，方便我们进行接口测试
                // 在生产环境中，对于需要身份认证的 POST/PUT 等请求，通常需要正确处理 CSRF
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
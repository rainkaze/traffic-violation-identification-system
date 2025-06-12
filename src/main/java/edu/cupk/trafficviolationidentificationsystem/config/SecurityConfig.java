package edu.cupk.trafficviolationidentificationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 启用并配置 CORS
                .cors(withDefaults())

                // 2. 配置请求授权规则
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/hello", "/api/**").permitAll() // 确保 /api/** 路径被允许
                                .anyRequest().authenticated()
                )

                // 3. 暂时禁用 CSRF 防护
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 4. 定义CORS的具体配置源 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许来自前端开发服务器的请求
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // 允许所有请求方法 (GET, POST, PUT, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许发送凭证 (如 cookies)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用这个CORS配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
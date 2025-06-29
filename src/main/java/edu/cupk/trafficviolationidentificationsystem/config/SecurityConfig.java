package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // 1. 合并所有需要公开访问的端点
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/test/**",
                                "/api/signal/**",
                                "/api/devices/streams",
                                "/uploads/**",
                                "/ws/**", // 开放WebSocket连接
                                // =========================================================
                                // ========== 在这里添加新的规则，允许报表API的匿名访问 ==========
                                "/api/reports/**"
                                // =========================================================
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/devices/bind").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/devices/*/status").permitAll()

                        // 2. 合并设备管理相关权限
                        .requestMatchers(HttpMethod.GET, "/api/devices/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/devices").hasRole("管理员")
                        .requestMatchers(HttpMethod.PUT, "/api/devices/**").hasRole("管理员")
                        .requestMatchers(HttpMethod.DELETE, "/api/devices/**").hasRole("管理员")

                        // 3. 合并数据和统计接口权限，并使用最全的角色列表
                        .requestMatchers("/api/violations/**", "/api/statistics/**").hasAnyRole("管理员", "警员", "小队长", "中队长", "大队长")

                        // 4. 保留管理员专属接口权限
                        .requestMatchers("/api/admin/**").hasRole("管理员")

                        // 5. 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许您的Vue前端访问
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
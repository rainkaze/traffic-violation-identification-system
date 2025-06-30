package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * [合并后] Spring Security 配置类.
 * <p>
 * 这是应用程序安全配置的核心，负责定义认证、授权、跨域(CORS)、CSRF保护、
 * 会话管理等安全策略。它结合了精细的角色权限控制和完整的API端点覆盖。
 * </p>
 * {@link EnableWebSecurity}: 启用Spring Security的Web安全支持。
 * {@link EnableMethodSecurity}: 启用方法级别的安全注解，如 @PreAuthorize。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        log.info("创建 PasswordEncoder Bean (BCryptPasswordEncoder)。");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("获取 AuthenticationManager Bean。");
        return configuration.getAuthenticationManager();
    }

    /**
     * [合并后] 配置安全过滤器链 (SecurityFilterChain).
     * <p>
     * 采用了版本一的精细化权限控制，并整合了版本二中新增的API端点（如/api/dashboard/**, /api/users/**），
     * 将它们纳入了原有的角色权限管理体系。
     * </p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("--- 开始配置 SecurityFilterChain ---");

        http
                .cors(withDefaults())
                .csrf(csrf -> {
                    csrf.disable();
                    log.info("CSRF 保护已禁用 (适用于无状态API)。");
                })
                .authorizeHttpRequests(authorize -> {
                            log.info("配置HTTP请求授权规则...");
                            authorize
                                    // 1. 公开访问端点：允许匿名访问以下路径
                                    .requestMatchers(
                                            "/api/auth/**",
                                            "/api/test/**",
                                            "/api/signal/**",
                                            "/api/devices/streams",
                                            "/uploads/**",
                                            "/ws/**",
                                            "/api/reports/**"
                                    ).permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/devices/bind").permitAll()
                                    .requestMatchers(HttpMethod.PUT, "/api/devices/*/status").permitAll()

                                    // 2. 设备管理权限：区分GET和其他操作
                                    .requestMatchers(HttpMethod.GET, "/api/devices/**").authenticated()
                                    .requestMatchers(HttpMethod.POST, "/api/devices").hasRole("管理员")
                                    .requestMatchers(HttpMethod.PUT, "/api/devices/**").hasRole("管理员")
                                    .requestMatchers(HttpMethod.DELETE, "/api/devices/**").hasRole("管理员")

                                    // 3. 核心业务权限：违章、统计和仪表盘数据，允许多种角色访问
                                    .requestMatchers(
                                            "/api/violations/**",
                                            "/api/statistics/**",
                                            "/api/dashboard/**" // 新增的仪表盘API
                                    ).hasAnyRole("管理员", "警员", "小队长", "中队长", "大队长")

                                    // 4. 管理员专属权限：包括用户管理
                                    .requestMatchers(
                                            "/api/admin/**",
                                            "/api/users/**" // 新增的用户管理API
                                    ).hasRole("管理员")

                                    // 5. 兜底规则：除了以上明确放行的，其他所有请求都必须经过认证
                                    .anyRequest().authenticated();
                            log.info("HTTP请求授权规则配置完成。");
                        }
                )
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    log.info("会话管理策略设置为: STATELESS。");
                });

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("已将 JwtAuthenticationFilter 添加到过滤器链。");
        log.info("--- SecurityFilterChain 配置完成 ---");

        return http.build();
    }

    /**
     * [合并后] 配置CORS（跨域资源共享）。
     * 采用版本一中更安全、更明确的配置，指定前端开发服务器地址。
     * 避免使用"*"通配符，以增强安全性。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("创建 CORS Configuration Bean。");
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的源地址列表 (这里是您的Vue前端开发服务器地址)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // 允许的HTTP方法 (GET, POST, etc.)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许发送凭证（如Cookies）
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        log.info("CORS 配置完成，允许源: '{}'", configuration.getAllowedOrigins());
        return source;
    }
}
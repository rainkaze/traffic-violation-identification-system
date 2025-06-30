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
 * Spring Security 配置类.
 * <p>
 * 这是应用程序安全配置的核心，负责定义认证、授权、跨域(CORS)、CSRF保护、
 * 会话管理等安全策略。
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

    /**
     * 构造函数注入自定义的JWT认证过滤器。
     * @param jwtAuthenticationFilter 用于在每个请求中校验JWT Token的过滤器。
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 定义密码编码器 Bean.
     * <p>
     * 使用 {@link BCryptPasswordEncoder} 作为密码编码器。这是一种强哈希算法，
     * 是目前存储密码的标准做法，可以有效防止密码被破解。
     * </p>
     * @return PasswordEncoder 实例。
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        log.info("创建 PasswordEncoder Bean (BCryptPasswordEncoder)。");
        return new BCryptPasswordEncoder();
    }

    /**
     * 定义认证管理器 Bean.
     * <p>
     * 从 Spring Security 的配置中获取默认的 {@link AuthenticationManager}，
     * 用于处理认证请求。
     * </p>
     * @param configuration Spring的认证配置。
     * @return AuthenticationManager 实例。
     * @throws Exception 如果获取失败。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("获取 AuthenticationManager Bean。");
        return configuration.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链 (SecurityFilterChain).
     * <p>
     * 这是定义HTTP请求安全策略的核心。它通过链式调用配置了各项安全规则。
     * </p>
     * @param http HttpSecurity 对象，用于构建安全配置。
     * @return 构建完成的 SecurityFilterChain。
     * @throws Exception 配置过程中可能抛出的异常。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("--- 开始配置 SecurityFilterChain ---");

        http
                // 启用CORS（跨域资源共享），并使用下面定义的 `corsConfigurationSource` Bean 的配置
                .cors(withDefaults())
                // 禁用CSRF（跨站请求伪造）保护。因为我们使用JWT进行无状态认证，所以CSRF攻击的风险较低。
                .csrf(csrf -> {
                    csrf.disable();
                    log.info("CSRF 保护已禁用 (适用于无状态API)。");
                })
                // 定义HTTP请求的授权规则
                .authorizeHttpRequests(authorize -> {
                            log.info("配置HTTP请求授权规则...");
                            authorize
                                    // 1. 公开访问端点：允许匿名访问以下路径
                                    .requestMatchers(
                                            "/api/auth/**",              // 认证相关API (登录/注册)
                                            "/api/test/**",              // 测试API
                                            "/api/signal/**",            // WebSocket信令API
                                            "/api/devices/streams",      // 获取设备流信息
                                            "/uploads/**",               // 静态资源访问 (如上传的文件)
                                            "/ws/**",                    // WebSocket连接端点
                                            "/api/reports/**"            // 报表API
                                    ).permitAll()
                                    // 以下两个特定路径也允许匿名访问
                                    .requestMatchers(HttpMethod.POST, "/api/devices/bind").permitAll()
                                    .requestMatchers(HttpMethod.PUT, "/api/devices/*/status").permitAll()

                                    // 2. 设备管理权限：区分GET和其他操作
                                    .requestMatchers(HttpMethod.GET, "/api/devices/**").authenticated() // 查看设备需要认证
                                    .requestMatchers(HttpMethod.POST, "/api/devices").hasRole("管理员")    // 增删改设备需要管理员角色
                                    .requestMatchers(HttpMethod.PUT, "/api/devices/**").hasRole("管理员")
                                    .requestMatchers(HttpMethod.DELETE, "/api/devices/**").hasRole("管理员")

                                    // 3. 核心业务权限：违章和统计数据，允许多种角色访问
                                    .requestMatchers("/api/violations/**", "/api/statistics/**").hasAnyRole("管理员", "警员", "小队长", "中队长", "大队长")

                                    // 4. 管理员专属权限
                                    .requestMatchers("/api/admin/**").hasRole("管理员")

                                    // 5. 兜底规则：除了以上明确放行的，其他所有请求都必须经过认证
                                    .anyRequest().authenticated();
                            log.info("HTTP请求授权规则配置完成。");
                        }
                )
                // 配置会话管理策略为“无状态”(STATELESS)。
                // 这告诉Spring Security不要创建或使用HttpSession，每次请求都依赖JWT进行认证。
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    log.info("会话管理策略设置为: STATELESS。");
                });

        // 将自定义的JWT过滤器添加到Spring Security的过滤器链中，
        // 放置在处理用户名/密码认证的过滤器之前。
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("已将 JwtAuthenticationFilter 添加到过滤器链。");
        log.info("--- SecurityFilterChain 配置完成 ---");

        return http.build();
    }

    /**
     * 配置CORS（跨域资源共享）。
     * <p>
     * 为了允许来自不同源（例如，运行在 http://localhost:5173 的Vue前端）的Web页面
     * 访问本应用的API，需要进行CORS配置。
     * </p>
     * @return CorsConfigurationSource 实例。
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
        // 是否允许发送凭证（如Cookies）
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用此CORS配置
        source.registerCorsConfiguration("/**", configuration);
        log.info("CORS 配置完成，允许源: '{}'", configuration.getAllowedOrigins());
        return source;
    }
}
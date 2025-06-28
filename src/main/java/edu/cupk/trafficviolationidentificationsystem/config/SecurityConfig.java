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
                        .requestMatchers("/api/test/**").permitAll() // 在其他规则之前添加这一行

                        // 公开认证端点
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/devices/bind").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/devices/*/status").permitAll()   // 允许设备匿名更新心跳状态
                        .requestMatchers("/api/signal/**").permitAll()
                        .requestMatchers("/api/devices/streams").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/devices/**").authenticated() // 任何登录用户都可以查看设备
                        .requestMatchers(HttpMethod.POST, "/api/devices").hasRole("管理员") // 只有管理员可以新建
                        .requestMatchers(HttpMethod.PUT, "/api/devices/**").hasRole("管理员")   // 只有管理员可以修改
                        .requestMatchers(HttpMethod.DELETE, "/api/devices/**").hasRole("管理员") // 只有管理员可以删除

                        .requestMatchers("/uploads/**").permitAll() // 测试视频流

                        // 为数据接口添加明确的访问角色
                        .requestMatchers("/api/violations/**", "/api/statistics/**").hasAnyRole("管理员", "警员", "中队长", "大队长")
                        // 只有 '管理员' 可以访问 admin 接口
                        .requestMatchers("/api/admin/**").hasRole("管理员")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // ===== 开始修改：临时允许所有来源 =====
    // 注意：为了调试，我们暂时允许所有来源。联调成功后，可以改回 List.of("http://localhost:5173")
    configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    // ===== 结束修改 =====

    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
}
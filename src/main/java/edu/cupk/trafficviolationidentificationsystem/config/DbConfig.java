package edu.cupk.trafficviolationidentificationsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据库连接配置类.
 * <p>
 * 使用 {@link ConfigurationProperties} 注解将 Spring Boot 配置文件中
 * 前缀为 {@code spring.datasource} 的属性值自动绑定到该类的字段上。
 * <p>
 * 示例 (application.yml):
 * <pre>
 * spring:
 * datasource:
 * url: jdbc:mysql://localhost:3306/mydatabase
 * username: root
 * password: password
 * </pre>
 *
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DbConfig {

    /**
     * JDBC 连接 URL.
     */
    private String url;

    /**
     * 数据库用户名.
     */
    private String username;

    /**
     * 数据库密码.
     */
    private String password;
}
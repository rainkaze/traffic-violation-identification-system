package edu.cupk.trafficviolationidentificationsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DbConfig {
    private String url;
    private String username;
    private String password;
}
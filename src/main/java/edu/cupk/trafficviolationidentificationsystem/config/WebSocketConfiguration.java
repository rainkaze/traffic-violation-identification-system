package edu.cupk.trafficviolationidentificationsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 端点扫描与注册配置 (基于注解).
 * <p>
 * 本配置类用于启用 Spring 对JSR-356标准 WebSocket 注解 (如 {@code @ServerEndpoint}) 的支持。
 * </p>
 */
@Configuration
public class WebSocketConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfiguration.class);

    /**
     * 注册一个 ServerEndpointExporter Bean.
     * <p>
     * 这个 Bean 会在 Spring 容器启动时自动扫描所有被 {@code @ServerEndpoint}
     * 注解标记的类，并将它们注册为 WebSocket 端点。
     * 如果没有这个 Bean，所有 {@code @ServerEndpoint} 注解都将无效。
     * </p>
     * @return ServerEndpointExporter 实例。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        log.info("创建 ServerEndpointExporter Bean，将自动扫描并注册 @ServerEndpoint 注解的 WebSocket 端点。");
        return new ServerEndpointExporter();
    }

}
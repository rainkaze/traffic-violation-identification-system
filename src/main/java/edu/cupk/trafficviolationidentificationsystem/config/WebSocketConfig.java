package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.ws.SignalingWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 处理器配置 (基于 Handler)。
 * <p>
 * 本配置类使用 Spring 的 {@link WebSocketConfigurer} 接口来手动注册 WebSocket 处理器。
 * 这种方式适用于需要对连接过程进行更精细控制的场景。
 * </p>
 * {@link EnableWebSocket}: 开启 Spring 对 WebSocket 的支持。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final SignalingWebSocketHandler signalingWebSocketHandler;

    /**
     * 构造函数注入信令处理器。
     * @param signalingWebSocketHandler 处理 WebRTC 信令消息的自定义 WebSocket 处理器。
     */
    public WebSocketConfig(SignalingWebSocketHandler signalingWebSocketHandler) {
        this.signalingWebSocketHandler = signalingWebSocketHandler;
    }

    /**
     * 注册 WebSocket 处理器到指定的路径。
     * @param registry 用于注册处理器的注册表。
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        final String path = "/api/signal";
        // 将信令处理器映射到路径 "/api/signal"
        registry.addHandler(signalingWebSocketHandler, path)
                // 设置允许所有来源的连接。
                // 【代码优化说明】
                // setAllowedOrigins("*") 是一个开放的配置，适合开发环境。
                // 在生产环境中，为了安全，应替换为具体的前端域名列表，例如:
                // .setAllowedOrigins("http://your-frontend-domain.com")
                .setAllowedOrigins("*");
        log.info("WebSocket 处理器 '{}' 已注册到路径 '{}'，允许所有来源连接。",
                signalingWebSocketHandler.getClass().getSimpleName(), path);
    }
}
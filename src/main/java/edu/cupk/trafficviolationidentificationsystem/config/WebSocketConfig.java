package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.ws.SignalingWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingWebSocketHandler signalingWebSocketHandler;

    public WebSocketConfig(SignalingWebSocketHandler signalingWebSocketHandler) {
        this.signalingWebSocketHandler = signalingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 关键改动：明确设置允许的来源，而不是依赖其他配置
        // "setAllowedOrigins("*")" 是最开放的配置，允许任何来源连接，非常适合开发和调试阶段
        registry.addHandler(signalingWebSocketHandler, "/api/signal")
                .setAllowedOrigins("*");
    }
}
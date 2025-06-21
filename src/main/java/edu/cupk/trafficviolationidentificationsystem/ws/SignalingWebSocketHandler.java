package edu.cupk.trafficviolationidentificationsystem.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SignalingWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionId(session);
        if (sessionId != null) {
            sessions.put(sessionId, session);
            logger.info("✅ WebSocket session ESTABLISHED for id: [{}]. Total sessions: {}", sessionId, sessions.size());
        } else {
            logger.warn("⛔ Connection established but no deviceId/viewerId found in URI. Closing session.");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = getSessionId(session);
        if (senderId == null) return;

        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            logger.info("✉️ Message RECEIVED from [{}]: {}", senderId, message.getPayload());

            // 消息体中必须包含 targetDeviceId
            Object targetIdObj = payload.get("targetDeviceId");
            if (targetIdObj == null) {
                logger.warn("⛔ Message from [{}] is missing 'targetDeviceId'. Discarding.", senderId);
                return;
            }
            String targetId = String.valueOf(targetIdObj);

            WebSocketSession targetSession = sessions.get(targetId);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(message);
                logger.info("✈️ Message FORWARDED from [{}] to [{}].", senderId, targetId);
            } else {
                logger.warn("⚠️ Target session for id [{}] not found or is closed. Cannot forward message.", targetId);
            }
        } catch (IOException e) {
            logger.error("❌ Error processing message from [{}]: {}", senderId, message.getPayload(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = getSessionId(session);
        if (sessionId != null) {
            sessions.remove(sessionId);
            logger.info("❌ WebSocket session CLOSED for id: [{}]. Reason: {}. Total sessions: {}", sessionId, status, sessions.size());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("B WebSocket transport ERROR for session [{}]: {}", getSessionId(session), exception.getMessage());
    }

    // 从URI中解析出ID，兼容deviceId和viewerId
    private String getSessionId(WebSocketSession session) {
        if (session.getUri() == null) return null;
        String query = session.getUri().getQuery();
        if (query == null) return null;

        // "deviceId=5" or "deviceId=viewer-xyz"
        if (query.startsWith("deviceId=")) {
            return query.substring("deviceId=".length());
        }
        return null;
    }
}
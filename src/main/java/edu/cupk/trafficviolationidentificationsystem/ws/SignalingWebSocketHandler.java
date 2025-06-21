// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/ws/SignalingWebSocketHandler.java

package edu.cupk.trafficviolationidentificationsystem.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cupk.trafficviolationidentificationsystem.service.DeviceService;
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
    private final DeviceService deviceService;

    public SignalingWebSocketHandler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionId(session);
        if (sessionId != null && !sessionId.isEmpty()) {
            sessions.put(sessionId, session);
            logger.info("✅ WebSocket session ESTABLISHED for id: [{}]. Total sessions: {}", sessionId, sessions.size());
            try {
                Integer deviceId = Integer.parseInt(sessionId);
                deviceService.updateDeviceStatus(deviceId, "ONLINE");
                logger.info("✅ Device status for ID [{}] updated to ONLINE.", deviceId);
            } catch (NumberFormatException e) {
                logger.info("ℹ️ Session for viewer [{}] connected, no status update needed.", sessionId);
            }
        } else {
            logger.warn("⛔ Connection established but no deviceId found in URI. Closing session.");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = getSessionId(session);
        if (senderId == null) return;

        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            // [最终修正] 总是注入发送者ID，便于客户端识别
            payload.put("fromId", senderId);

            String updatedMessage = objectMapper.writeValueAsString(payload);
            logger.info("✉️ Message RECEIVED from [{}]: {}", senderId, message.getPayload());

            Object targetIdObj = payload.get("targetDeviceId");

            // 1. 定向发送逻辑 (用于 offer, answer)
            if (targetIdObj != null && !String.valueOf(targetIdObj).isEmpty()) {
                String targetId = String.valueOf(targetIdObj);
                WebSocketSession targetSession = sessions.get(targetId);
                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(updatedMessage));
                    logger.info("✈️ Message FORWARDED from [{}] to [{}].", senderId, targetId);
                } else {
                    logger.warn("⚠️ Target session for id [{}] not found or is closed.", targetId);
                }
            } else {
                // 2. 广播逻辑 (用于 ice-candidate)
                logger.info("Broadcasting message from [{}] to all other clients.", senderId);
                for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                    if (!entry.getKey().equals(senderId) && entry.getValue().isOpen()) {
                        entry.getValue().sendMessage(new TextMessage(updatedMessage));
                        logger.info("✈️ Message BROADCASTED from [{}] to [{}].", senderId, entry.getKey());
                    }
                }
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
            try {
                Integer deviceId = Integer.parseInt(sessionId);
                deviceService.updateDeviceStatus(deviceId, "OFFLINE");
                logger.info("❌ Device status for ID [{}] updated to OFFLINE.", deviceId);
            } catch (NumberFormatException e) {
                logger.info("ℹ️ Session for viewer [{}] closed, no status update needed.", sessionId);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Transport ERROR for session [{}]: {}", getSessionId(session), exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    private String getSessionId(WebSocketSession session) {
        if (session.getUri() == null) return null;
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("deviceId=")) {
            return query.substring("deviceId=".length());
        }
        return null;
    }
}
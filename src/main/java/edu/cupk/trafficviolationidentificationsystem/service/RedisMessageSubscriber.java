package edu.cupk.trafficviolationidentificationsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.List;

@Service
public class RedisMessageSubscriber implements MessageListener {

    @Autowired
    private WebSocketServer webSocketServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Map<String, Object> notificationData = objectMapper.readValue(message.getBody(), Map.class);
            List<Integer> recipientIds = (List<Integer>) notificationData.get("recipientIds");
            String notificationMessage = (String) notificationData.get("message");

            // 通过 WebSocket 推送给客户端
            webSocketServer.sendToClientsByInt(recipientIds, notificationMessage);

        } catch (IOException e) {
            // handle exception
        }
    }
}
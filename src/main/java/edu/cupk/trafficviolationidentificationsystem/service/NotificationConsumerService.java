package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import edu.cupk.trafficviolationidentificationsystem.controller.NotificationController.NotificationRequest; // 复用您已有的类
import edu.cupk.trafficviolationidentificationsystem.entity.Notification;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationMapper;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationConsumerService {

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private NotificationMapper notificationMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICATION)
    public void receiveNotification(NotificationRequest request) {
        System.out.println(" [x] Received notification request: " + request);

        // --- 这里是您原来 NotificationController 中 /send 接口的核心逻辑 ---
        String messageContent = "主题:" + request.getSubject() + "  内容:" + request.getMessage();

        // 1. 通过WebSocket实时发送
        webSocketServer.sendToClientsByInt(request.getRecipientIds(), messageContent);

        // 2. 持久化到数据库
        for (Integer recipientId : request.getRecipientIds()) {
            Notification notification = Notification.builder()
                    .userId(Long.valueOf(recipientId))
                    .message(messageContent)
                    .timestamp(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationMapper.insertNotification(notification);
        }
        System.out.println("Notification processed and stored successfully.");
    }
}
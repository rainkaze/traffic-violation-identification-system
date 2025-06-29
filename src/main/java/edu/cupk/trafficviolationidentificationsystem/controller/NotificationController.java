package edu.cupk.trafficviolationidentificationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.config.RedisPubSubConfig;
import edu.cupk.trafficviolationidentificationsystem.entity.Notification;
import edu.cupk.trafficviolationidentificationsystem.entity.NotificationSetting;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * [合并后] 通知管理控制器。
 * 负责处理用户的通知获取、标记已读、设置以及通过WebSocket和Redis发送通知等操作。
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NotificationSettingMapper notificationSettingMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    // 1. 注入RedisTemplate
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    // 2. 实例化ObjectMapper用于序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据用户ID获取其所有通知列表。
     *
     * @param userId 目标用户的ID。
     * @return 该用户的通知列表。
     */
    @GetMapping
    public List<Notification> getNotificationsByUserId(@RequestParam Long userId) {
        return notificationMapper.getAllNotificationsByUserId(userId);
    }

    /**
     * 将指定用户的所有未读通知标记为已读。
     *
     * @param request 包含 "userId" 的请求体。
     * @return 操作成功的字符串 "success"。
     */
    @PutMapping("/markAllAsRead")
    @AuditLog(actionType = "MARK_ALL_NOTIFICATIONS_READ", targetEntityType = "USER_NOTIFICATIONS", targetEntityIdExpression = "#request.userId")
    public String markAllAsRead(@RequestBody MarkReadRequest request) {
        notificationMapper.markAllAsRead(request.getUserId());
        return "success";
    }

    /**
     * 内部接口，用于直接向数据库插入一条通知。
     *
     * @param notification 要插入的通知实体。
     * @return 操作成功的字符串 "success"。
     */
    @PostMapping("/insert")
    @AuditLog(actionType = "INSERT_NOTIFICATION_INTERNAL", targetEntityType = "NOTIFICATION", targetEntityIdExpression = "#notification.userId")
    public String insertNotification(@RequestBody Notification notification) {
        notificationMapper.insertNotification(notification);
        return "success";
    }

    /**
     * 为用户初次设置通知偏好。
     *
     * @param settings 通知设置列表。
     * @return 操作成功的字符串 "success"。
     */
    @PostMapping("/set")
    @AuditLog(actionType = "SET_NOTIFICATION_SETTINGS", targetEntityType = "NOTIFICATION_SETTINGS", targetEntityIdExpression = "#authentication.principal.userId")
    public String setNotification(@RequestBody List<NotificationSetting> settings) {
        notificationSettingMapper.insertNotificationSetting(settings);
        return "success";
    }

    /**
     * 更新用户已存在的通知偏好设置。
     *
     * @param settings 更新后的通知设置列表。
     * @return 操作成功的字符串 "success"。
     */
    @PostMapping("/put")
    @AuditLog(actionType = "UPDATE_NOTIFICATION_SETTINGS", targetEntityType = "NOTIFICATION_SETTINGS", targetEntityIdExpression = "#authentication.principal.userId")
    public String putNotification(@RequestBody List<NotificationSetting> settings) {
        notificationSettingMapper.putNotificationSetting(settings);
        return "success";
    }

    /**
     * [合并后] 发送通知给一个或多个指定用户。
     * 此操作会通过WebSocket实时推送，将通知存入数据库，并通过Redis发布消息。
     *
     * @param request 包含主题、内容和接收者ID列表的请求对象。
     * @return 操作成功的响应实体。
     */
    @PostMapping("/send")
    @AuditLog(actionType = "SEND_NOTIFICATION", targetEntityType = "NOTIFICATION_RECIPIENTS", targetEntityIdExpression = "#request.recipientIds.toString()")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        // 3. 使用规范化的消息格式
        String messageContent = "主题:" + request.getSubject() + "  内容:" + request.getMessage();

        // 4. 通过WebSocket实时发送
        webSocketServer.sendToClientsByInt(request.getRecipientIds(), messageContent);

        // 5. 持久化到数据库
        for (Integer recipientId : request.getRecipientIds()) {
            Notification notification = Notification.builder()
                    .userId(Long.valueOf(recipientId))
                    .message(messageContent)
                    .timestamp(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationMapper.insertNotification(notification);
        }

        // 6. 将消息发布到 Redis Topic
        try {
            Map<String, Object> messageMap = Map.of(
                    "recipientIds", request.getRecipientIds(),
                    "message", messageContent
            );
            // 使用 convertAndSend 发送到指定的主题
            redisTemplate.convertAndSend(RedisPubSubConfig.NOTIFICATION_TOPIC, objectMapper.writeValueAsString(messageMap));
        } catch (Exception e) {
            // 在实际项目中，这里应该记录错误日志
            e.printStackTrace();
        }

        return ResponseEntity.ok("通知已成功发送、存储和发布");
    }

    /**
     * 用于/markAllAsRead接口的请求体封装类。
     */
    @Data
    public static class MarkReadRequest {
        private Long userId;
    }

    /**
     * 用于/send接口的请求体封装类。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationRequest {
        private String subject;
        private String message;
        private List<Integer> recipientIds;
    }
}
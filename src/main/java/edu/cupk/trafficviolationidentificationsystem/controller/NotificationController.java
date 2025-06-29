package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知管理控制器。
 * 负责处理用户的通知获取、标记已读、设置以及发送通知等操作。
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
        Long userId = request.getUserId();
        notificationMapper.markAllAsRead(userId);
        return "success";
    }

    /**
     * 内部接口，用于直接向数据库插入一条通知。
     * 通常由系统内部服务调用，而非前端直接调用。
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
     * 发送通知给一个或多个指定用户。
     * 此操作会通过WebSocket实时推送，并同时将通知存入数据库。
     *
     * @param request 包含主题、内容和接收者ID列表的请求对象。
     * @return 操作成功的响应实体。
     */
    @PostMapping("/send")
    @AuditLog(actionType = "SEND_NOTIFICATION", targetEntityType = "NOTIFICATION_RECIPIENTS", targetEntityIdExpression = "#request.recipientIds.toString()")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        String messageContent = "主题:" + request.getSubject() + "  内容:" + request.getMessage();

        // 通过WebSocket实时发送
        webSocketServer.sendToClientsByInt(request.getRecipientIds(), messageContent);

        // 持久化到数据库
        for (Integer recipientId : request.getRecipientIds()) {
            Notification notification = Notification.builder()
                    .userId(Long.valueOf(recipientId))
                    .message(messageContent)
                    .timestamp(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationMapper.insertNotification(notification);
        }
        return ResponseEntity.ok("通知已成功发送和存储");
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
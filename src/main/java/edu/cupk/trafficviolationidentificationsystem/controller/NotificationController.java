package edu.cupk.trafficviolationidentificationsystem.controller;

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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    // GET /api/notifications?userId=xxx
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NotificationSettingMapper notificationSettingMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping
    public List<Notification> getNotificationsByUserId(@RequestParam Long userId) {

        System.out.println("获取用户 " + userId + " 的通知列表");
        // 这里先写死静态数据，后续改为数据库查询
        List<Notification> notifications = new ArrayList<>();
        //未读消息红点由前端动态计算得到 这里只用获取所有通知即可
        notifications=notificationMapper.getAllNotificationsByUserId(userId);
        return notifications;
    }

    // PUT /api/notifications/markAllAsRead
    @PutMapping("/markAllAsRead")
    public String markAllAsRead(@RequestBody MarkReadRequest request) {
        Long userId = request.getUserId();
        // 这里写死逻辑，实际需要数据库操作将该用户所有未读通知标记为已读
        System.out.println("标记用户 " + userId + " 的所有通知为已读");
        notificationMapper.markAllAsRead(userId);
        return "success";
    }

    public static class MarkReadRequest {
        private Long userId;
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

    //插入数据库
    @PostMapping("/insert")
    public String insertNotification(@RequestBody Notification notification) {
        // 这里写死逻辑，实际需要数据库操作将该用户所有未读通知标记为已读
        notificationMapper.insertNotification(notification);
        System.out.println("插入通知：" + notification);
        return "success";
    }

//    设置通知
    @PostMapping("/set")
    public String setNotification(@RequestBody List<NotificationSetting> settings) {
        // 这里写死逻辑，实际需要数据库操作将该用户所有未读通知标记为已读
        System.out.println("在设置了");
        System.out.println("设置通知：" + settings);

        notificationSettingMapper.insertNotificationSetting(settings);

        return "success";
    }

//    @PostMapping("/set")
//    public String setNotification(@RequestBody String rawJson) {
//        System.out.println("收到原始请求体：" + rawJson);
//        return "ok";
//    }
    @PostMapping("/put")
    public String putNotification(@RequestBody List<NotificationSetting> settings) {
        // 这里写死逻辑，实际需要数据库操作将该用户所有未读通知标记为已读
        System.out.println("在更新了");
        System.out.println("更新：" + settings);

        notificationSettingMapper.putNotificationSetting(settings);
        List<Integer> userIds =notificationSettingMapper.getUserIdsByTypeKey("password_change_alert");
        System.out.println("用户的ids："+userIds);


        return "success";
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        // 打印接收到的数据（前期调试用）
        System.out.println("接收到通知请求：");
        System.out.println("主题: " + request.getSubject());
        System.out.println("内容: " + request.getMessage());
        System.out.println("收件人ID列表: " + request.getRecipientIds());


        List<Integer> a=new ArrayList<>();
        a=request.getRecipientIds();
        webSocketServer.sendToClientsByInt(a,"主题"+request.getSubject()+"  内容  "+request.getMessage());
//        webSocketServer.sendToAllClient("你好啊");
//        websocket发通知是在连接的情况下可以即时发 不管在线离线 你都要存通知数据库里

        for (Integer recipientId : request.getRecipientIds()) {
            Notification notification = Notification.builder()
                    .userId(Long.valueOf(recipientId))
                    .message("主题" + request.getSubject() + "  内容  " + request.getMessage())
                    .timestamp(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationMapper.insertNotification(notification);
            System.out.println("插入通知：" + notification);

        }
        // 这里只返回一个简单的响应
        return ResponseEntity.ok("通知接收成功");
    }

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

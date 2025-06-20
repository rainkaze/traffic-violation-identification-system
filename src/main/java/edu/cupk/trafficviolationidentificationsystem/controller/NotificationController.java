package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.entity.Notification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    // GET /api/notifications?userId=xxx
    @GetMapping
    public List<Notification> getNotificationsByUserId(@RequestParam Long userId) {

//        // 数据库伪代码
//        SELECT * FROM notifications
//        WHERE user_id = #{userId}
//        ORDER BY timestamp DESC;

        System.out.println("获取用户 " + userId + " 的通知列表");
        // 这里先写死静态数据，后续改为数据库查询
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(1L, userId, "欢迎使用系统！", LocalDateTime.now().minusDays(1), false));
        notifications.add(new Notification(2L, userId, "您的订单已发货。", LocalDateTime.now().minusDays(2), true));
        notifications.add(new Notification(3L, userId, "系统维护将在周末进行。", LocalDateTime.now().minusHours(3), false));

        notifications.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

        return notifications;
    }

    // PUT /api/notifications/markAllAsRead
    @PutMapping("/markAllAsRead")
    public String markAllAsRead(@RequestBody MarkReadRequest request) {
        Long userId = request.getUserId();
        // 这里写死逻辑，实际需要数据库操作将该用户所有未读通知标记为已读
        System.out.println("标记用户 " + userId + " 的所有通知为已读");
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
        System.out.println("插入通知：" + notification);
        return "success";
    }

}

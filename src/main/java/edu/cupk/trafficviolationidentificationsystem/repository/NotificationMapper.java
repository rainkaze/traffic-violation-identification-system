package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    //查询用户的未读通知
    List<Notification> getAllNotificationsByUserId(Long userId);

    void markAllAsRead(Long userId);

    void insertNotification(Notification notification);
}

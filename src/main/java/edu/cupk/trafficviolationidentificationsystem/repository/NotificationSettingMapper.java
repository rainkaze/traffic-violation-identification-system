package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.entity.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationSettingMapper {
    void insertNotificationSetting(List<NotificationSetting> settings);

    void putNotificationSetting(List<NotificationSetting> settings);

    List<Integer> getUserIdsByTypeKey(String typeKey);

    List<NotificationSetting> selectByUserId(Long userId);

    void deleteByUserId(Long userId);
}

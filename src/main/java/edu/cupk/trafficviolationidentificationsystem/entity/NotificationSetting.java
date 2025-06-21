package edu.cupk.trafficviolationidentificationsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {
    private Integer id;
    private Integer userId;
    private String typeKey;
    private Boolean enabled;
}

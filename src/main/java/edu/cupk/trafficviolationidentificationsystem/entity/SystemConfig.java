package edu.cupk.trafficviolationidentificationsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
    private Long id;
    private String systemName;
    private Integer sessionTimeout;
    private Integer dataRetentionDays;
}

package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class TrafficRuleDto {
    private Integer ruleId;
    private String violationType;
    private Integer baseDemeritPoints; // 新增扣分信息
}
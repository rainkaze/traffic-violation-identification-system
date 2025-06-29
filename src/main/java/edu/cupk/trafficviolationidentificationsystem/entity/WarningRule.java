package edu.cupk.trafficviolationidentificationsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningRule {
    private Integer id;
    private String level;          // 预警等级
    private String violationType;  // 违法类型
    private Double minConfidence;  // 最低置信度
    private String description;    // 规则描述
}

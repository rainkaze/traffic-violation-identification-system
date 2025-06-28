package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationProcessingLog {
    private Long logId;
    private Long violationId;
    private Integer workflowId;
    private Integer nodeId;
    private Integer assignedUserId;
    private String status; // '待处理', '已完成', '已驳回'
    private String remarks;
    private LocalDateTime arrivedAt;
    private LocalDateTime processedAt;
}
package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLog {
    private Long logId;
    private Integer userId;
    private String actionType;
    private String targetEntityType;
    private String targetEntityId;
    private String details;
    private String clientIpAddress;
    private LocalDateTime createdAt;
}
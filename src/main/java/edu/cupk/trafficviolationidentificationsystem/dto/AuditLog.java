package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    private Long logId;
    private Integer userId;
    private String actionType;
    private String targetEntityType;
    private String targetEntityId;
    private String details;
    private String clientIpAddress;
    private LocalDateTime createdAt;

    // Getters and Setters
}

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
public class AudilVo {
    private Long logId;
    private String username;      //用户姓名非id
    private String actionType;
    private String targetEntityType;
    private String targetEntityId;
    private String details;
    private String clientIpAddress;
    private LocalDateTime createdAt;

}
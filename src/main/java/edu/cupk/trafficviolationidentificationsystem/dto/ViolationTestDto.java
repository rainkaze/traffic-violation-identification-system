package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationTestDto {
    private String plateNumber;
    private LocalDateTime violationTime;
    private Integer deviceId;
    private Integer ruleId;
}
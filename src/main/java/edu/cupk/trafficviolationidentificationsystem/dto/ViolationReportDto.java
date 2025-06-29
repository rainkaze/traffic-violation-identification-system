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
public class ViolationReportDto {
    private Long violationId;
    private String plateNumber;
    private String violationType;
    private LocalDateTime violationTime;
    private String location;      // 显示地点（设备地址）
    private String status;
}

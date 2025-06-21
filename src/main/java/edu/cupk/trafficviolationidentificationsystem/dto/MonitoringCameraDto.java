package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class MonitoringCameraDto {
    private Integer deviceId;
    private String deviceCode;
    private String deviceName;
    private String address;
    private String status;
    private String imageUrl; // 用于前端展示的图片URL
    private int violationCount; // 关联的违法数量（模拟）
}
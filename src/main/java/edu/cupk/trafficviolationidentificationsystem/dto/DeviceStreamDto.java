package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class DeviceStreamDto {
    private Integer deviceId;
    private String rtspUrl;
}
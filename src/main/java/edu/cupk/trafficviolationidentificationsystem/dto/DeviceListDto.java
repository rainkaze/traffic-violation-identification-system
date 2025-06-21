package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class DeviceListDto {
    private Integer deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String districtName; // 注意：这里是辖区名称，而非ID
    private String address;
    private String status;
    private String ipAddress;
}
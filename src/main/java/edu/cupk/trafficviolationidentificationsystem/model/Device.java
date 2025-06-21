package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Device {
    private Integer deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private Integer districtId;
    private String address;
    private Double latitude;
    private Double longitude;
    private String modelName;
    private String ipAddress;
    private String status;
    private LocalDate installedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // [新增]
    private String bindingCode;
    private LocalDateTime bindingCodeExpiresAt;
    private LocalDateTime boundAt;
}
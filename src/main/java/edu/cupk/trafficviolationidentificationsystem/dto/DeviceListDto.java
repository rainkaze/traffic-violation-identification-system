package edu.cupk.trafficviolationidentificationsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceListDto {
    private Integer deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String districtName; // 注意：这里是辖区名称，而非ID
    private String address;
    private String status;
    private String rtspUrl;
    private String modelName; // 在 DeviceFormView 加载时需要

    private String jurisdiction;
    private Double longitude;
    private Double latitude;
}
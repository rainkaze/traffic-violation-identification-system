package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceUpsertDto {
    @NotEmpty(message = "设备编码不能为空")
    private String deviceCode;

    @NotEmpty(message = "设备名称不能为空")
    private String deviceName;

    @NotEmpty(message = "设备类型不能为空")
    private String deviceType;

    @NotNull(message = "必须关联一个辖区")
    private Integer districtId;

    @NotEmpty(message = "设备地址不能为空")
    private String address;

    private String modelName;
    private String ipAddress;

    private String status;
}
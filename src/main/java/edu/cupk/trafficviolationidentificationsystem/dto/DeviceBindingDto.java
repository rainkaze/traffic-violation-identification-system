package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeviceBindingDto {
    @NotEmpty(message = "绑定码不能为空")
    private String bindingCode;
}
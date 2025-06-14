package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @NotEmpty(message = "当前密码不能为空")
    private String currentPassword;

    @NotEmpty(message = "新密码不能为空")
    @Size(min = 8, message = "新密码长度至少为8位")
    private String newPassword;
}
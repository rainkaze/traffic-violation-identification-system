package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfileUpdateDto {
    @NotEmpty(message = "姓名不能为空")
    private String fullName;

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
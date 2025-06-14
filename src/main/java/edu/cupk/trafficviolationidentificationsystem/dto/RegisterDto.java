package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String fullName;
    private String username;
    private String email;
    private String rank; // e.g., "警员"
    private String verificationCode; // 新增验证码字段
}
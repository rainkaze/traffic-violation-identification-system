package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);
    String register(RegisterDto registerDto);
    void sendVerificationCode(String email); // 新增发送验证码接口
    void logout(String token);
}
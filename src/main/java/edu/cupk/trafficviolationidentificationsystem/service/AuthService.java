package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}
// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/controller/AuthController.java
package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;
import edu.cupk.trafficviolationidentificationsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口
     * 通过 try-catch 捕获特定的认证异常，并返回更友好的错误信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponseDto jwtAuthResponse = authService.login(loginDto);
            return ResponseEntity.ok(jwtAuthResponse);
        } catch (BadCredentialsException e) {
            // 捕获密码错误异常
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误。"));
        } catch (DisabledException e) {
            // 捕获账户被禁用或待审批异常
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "账户已被禁用或正在等待管理员批准。"));
        } catch (Exception e) {
            // 捕获其他未知异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "登录时发生未知错误。"));
        }
    }

    /**
     * 用户注册申请接口
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        try {
            String response = authService.register(registerDto);
            return new ResponseEntity<>(Map.of("message", response), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
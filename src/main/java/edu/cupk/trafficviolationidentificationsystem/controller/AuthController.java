package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;
import edu.cupk.trafficviolationidentificationsystem.service.AuthService;
import edu.cupk.trafficviolationidentificationsystem.service.CounterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CounterService counterService;

    // 【新增】注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public AuthController(AuthService authService, CounterService counterService) {
        this.authService = authService;
        this.counterService = counterService;
    }

    /**
     * 【已重构】将发送验证码的任务提交到消息队列。
     *
     * @param payload 包含 "email" 键的请求体。
     * @return 立刻返回一个表示任务已接受的响应。
     */
    @PostMapping("/send-verification-code")
    @AuditLog(actionType = "SEND_VERIFICATION_CODE", targetEntityType = "EMAIL", targetEntityIdExpression = "#payload['email']")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "邮箱不能为空。"));
        }

        // 将邮件地址作为消息，发送到指定的交换机和路由键
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_EMAIL_VERIFICATION,
                email
        );

        // 立刻返回响应，告知前端任务已提交
        return ResponseEntity.ok(Map.of("message", "验证码发送请求已提交，请稍后查收邮件。"));
    }

    // login, register, logout 等其他方法保持不变...

    @PostMapping("/login")
    @AuditLog(actionType = "LOGIN", targetEntityType = "USER", targetEntityIdExpression = "#loginDto.username")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponseDto jwtAuthResponse = authService.login(loginDto);
            counterService.increment("logins:total");
            return ResponseEntity.ok(jwtAuthResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误。"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "账户已被禁用或正在等待管理员批准。"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "登录时发生未知错误。"));
        }
    }

    @PostMapping("/register")
    @AuditLog(actionType = "REGISTER", targetEntityType = "USER", targetEntityIdExpression = "#registerDto.username")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        try {
            String response = authService.register(registerDto);
            return new ResponseEntity<>(Map.of("message", response), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @AuditLog(actionType = "LOGOUT", targetEntityType = "USER")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        authService.logout(bearerToken);
        return ResponseEntity.ok(Map.of("message", "您已成功退出登录。"));
    }
}
package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;
import edu.cupk.trafficviolationidentificationsystem.service.AuthService;
import edu.cupk.trafficviolationidentificationsystem.service.CounterService; // 1. 引入CounterService
import jakarta.servlet.http.HttpServletRequest; // 2. 引入HttpServletRequest
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * [合并后] 处理用户认证（登录、注册、登出）和授权相关操作的控制器。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CounterService counterService; // 3. 注入CounterService

    /**
     * [合并后] 构造函数，注入认证服务和计数服务。
     *
     * @param authService    认证服务实例。
     * @param counterService 计数服务实例。
     */
    public AuthController(AuthService authService, CounterService counterService) {
        this.authService = authService;
        this.counterService = counterService;
    }

    /**
     * 发送邮箱验证码。
     * 用于用户注册或密码重置前的邮箱所有权验证。
     *
     * @param payload 包含 "email" 键的请求体。
     * @return 成功或失败的响应信息。
     */
    @PostMapping("/send-verification-code")
    @AuditLog(actionType = "SEND_VERIFICATION_CODE", targetEntityType = "EMAIL", targetEntityIdExpression = "#payload['email']")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "邮箱不能为空。"));
            }
            authService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("message", "验证码已成功发送至您的邮箱。"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 用户登录。
     * 验证用户凭据，成功后返回JWT令牌，并增加登录计数。
     *
     * @param loginDto 包含用户名和密码的登录数据对象。
     * @return 成功时返回JWT令牌；失败时返回相应的错误信息和HTTP状态码。
     */
    @PostMapping("/login")
    @AuditLog(actionType = "LOGIN", targetEntityType = "USER", targetEntityIdExpression = "#loginDto.username")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponseDto jwtAuthResponse = authService.login(loginDto);
            // 4. 添加登录计数功能
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

    /**
     * 用户注册。
     * 创建一个新用户账户，账户初始状态为待审核。
     *
     * @param registerDto 包含注册所需信息的数据对象。
     * @return 注册成功或失败的响应信息。
     */
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

    /**
     * [新增] 用户登出。
     * 使当前用户的JWT令牌失效。
     *
     * @param request HTTP请求对象，用于获取Authorization头。
     * @return 成功登出的响应信息。
     */
    @PostMapping("/logout")
    @AuditLog(actionType = "LOGOUT", targetEntityType = "USER") // 建议为登出也加上审计日志
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        authService.logout(bearerToken);
        return ResponseEntity.ok(Map.of("message", "您已成功退出登录。"));
    }
}
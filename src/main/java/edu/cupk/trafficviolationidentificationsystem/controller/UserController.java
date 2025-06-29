package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.PasswordChangeDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ProfileUpdateDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // 确保导入
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户个人信息管理控制器。
 * 提供用户查询个人信息、更新个人资料和修改密码等功能。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    /**
     * 构造函数，用于注入所需的服务和Mapper。
     *
     * @param userMapper  用户数据访问接口
     * @param userService 用户核心业务服务
     */
    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * 获取当前登录用户的完整信息。
     *
     * @return 包含当前用户信息（包括所辖区域）的DTO。如果用户未找到，返回404 Not Found。
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.findByUsername(username)
                .map(user -> {
                    UserDto dto = new UserDto(user);
                    dto.setDistricts(userMapper.findDistrictsByUserId(user.getUserId()));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 根据用户名获取指定用户的公开信息。
     *
     * @param username 要查询的用户名。
     * @return 包含该用户信息的DTO。如果用户未找到，返回404 Not Found。
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userMapper.findByUsername(username)
                .map(user -> {
                    UserDto dto = new UserDto(user);
                    dto.setDistricts(userMapper.findDistrictsByUserId(user.getUserId()));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 更新当前登录用户的个人资料。
     */
    @PutMapping("/profile")
    @AuditLog(actionType = "UPDATE_PROFILE", targetEntityType = "USER", targetEntityIdExpression = "#authentication.principal.userId")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto) {
        // 从 SecurityContextHolder 直接获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto updatedUser = userService.updateProfile(username, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 修改当前登录用户的密码。
     */
    @PostMapping("/profile/change-password")
    @AuditLog(actionType = "CHANGE_PASSWORD", targetEntityType = "USER", targetEntityIdExpression = "#authentication.principal.userId")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            // 从 SecurityContextHolder 直接获取用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, passwordChangeDto);
            return ResponseEntity.ok(Map.of("message", "密码修改成功。"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 根据关键字搜索用户。
     *
     * @param keyword 搜索关键字（可选）。如果为空，则返回所有用户。
     * @return 匹配的用户列表。
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String keyword) {
        List<User> users;
        if (keyword == null || keyword.trim().isEmpty()) {
            users = userMapper.getAllUsers();
        } else {
            users = userMapper.searchUsersByKeyword(keyword);
        }
        return ResponseEntity.ok(users);
    }
}
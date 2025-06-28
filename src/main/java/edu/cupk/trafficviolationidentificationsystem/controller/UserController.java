package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PasswordChangeDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ProfileUpdateDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.service.EmailService;
import edu.cupk.trafficviolationidentificationsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // --- 合并后的依赖 ---
    private final UserMapper userMapper;
    private final UserService userService;
    private final NotificationSettingMapper notificationSettingMapper;
    private final EmailService emailService;

    // 使用构造函数注入所有依赖，这是推荐的最佳实践
    public UserController(UserMapper userMapper, UserService userService, NotificationSettingMapper notificationSettingMapper, EmailService emailService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.notificationSettingMapper = notificationSettingMapper;
        this.emailService = emailService;
    }

    /**
     * 根据用户名获取用户信息，并附带其管理的辖区信息。
     * (采纳自版本二，功能更完整)
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // 查询该用户的辖区ID列表
        List<Integer> districtIds = userMapper.findDistrictIdsByUserId(user.getUserId());
        // 将辖区ID转换为字符串列表
        List<String> districtStrings = districtIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto); // 复制基础属性
        dto.setDistricts(districtStrings); // 设置辖区信息

        return ResponseEntity.ok(dto);
    }

    /**
     * 更新当前登录用户的个人资料。
     * (两个版本相同，保留其一)
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto updatedUser = userService.updateProfile(username, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 修改当前登录用户的密码，并在成功后根据用户设置发送邮件通知。
     * (采纳自版本一，包含邮件通知逻辑)
     */
    @PostMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, passwordChangeDto);

            // 成功后，检查是否需要发送邮件通知
            Optional<User> optionalUser = userMapper.findByUsername(username);
            if (optionalUser.isPresent()) {
                Integer userId = optionalUser.get().getUserId();
                // 检查用户是否开启了“密码修改”的通知
                List<Integer> userIdsWithSettingEnabled = notificationSettingMapper.getUserIdsByTypeKey("password_change_alert");
                if (userIdsWithSettingEnabled.contains(userId)) {
                    String email = optionalUser.get().getEmail();
                    emailService.sendSimpleMessage(email, "密码修改成功提醒", "您的账户密码已于 " + java.time.LocalDateTime.now() + " 成功修改。如果不是您本人操作，请立即联系管理员。");
                }
            }

            return ResponseEntity.ok(Map.of("message", "密码修改成功。"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 获取当前登录用户的ID。
     * (采纳自版本一，为前端提供必要的用户ID)
     */
    @GetMapping("/getUserId")
    public ResponseEntity<Integer> getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userMapper.findByUsername(username)
                    .map(User::getUserId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 根据关键字搜索用户，用于“发布任务”等功能。
     * (采纳自版本一，支持前端的搜索功能)
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String keyword) {
        List<User> users;
        if (keyword == null || keyword.trim().isEmpty()) {
            users = userMapper.getAllUsers(); // 假设 userMapper 中有此方法
        } else {
            users = userMapper.searchUsersByKeyword(keyword); // 假设 userMapper 中有此方法
        }
        return ResponseEntity.ok(users);
    }
}
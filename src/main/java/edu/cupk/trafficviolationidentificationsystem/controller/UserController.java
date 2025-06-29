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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final NotificationSettingMapper notificationSettingMapper;
    private final EmailService emailService;

    public UserController(UserMapper userMapper, UserService userService, NotificationSettingMapper notificationSettingMapper, EmailService emailService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.notificationSettingMapper = notificationSettingMapper;
        this.emailService = emailService;
    }

    /**
     * [新增] 获取当前登录用户的所有信息 (替代 /getUserId)
     * 这是一个更符合RESTful规范的做法。
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.findByUsername(username)
                .map(user -> {
                    List<Integer> districtIds = userMapper.findDistrictIdsByUserId(user.getUserId());
                    List<String> districtStrings = districtIds.stream().map(String::valueOf).collect(Collectors.toList());
                    UserDto dto = new UserDto();
                    BeanUtils.copyProperties(user, dto);
                    dto.setDistricts(districtStrings);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 根据用户名获取指定用户的信息 (用于查看他人信息)
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userMapper.findByUsername(username)
                .map(user -> {
                    List<Integer> districtIds = userMapper.findDistrictIdsByUserId(user.getUserId());
                    List<String> districtStrings = districtIds.stream().map(String::valueOf).collect(Collectors.toList());
                    UserDto dto = new UserDto();
                    BeanUtils.copyProperties(user, dto);
                    dto.setDistricts(districtStrings);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 更新当前登录用户的个人资料
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto updatedUser = userService.updateProfile(username, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 修改当前登录用户的密码
     */
    @PostMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, passwordChangeDto);

            userMapper.findByUsername(username).ifPresent(user -> {
                List<Integer> userIdsWithSettingEnabled = notificationSettingMapper.getUserIdsByTypeKey("password_change_alert");
                if (userIdsWithSettingEnabled.contains(user.getUserId())) {
                    emailService.sendSimpleMessage(user.getEmail(), "密码修改成功提醒", "您的账户密码已于 " + java.time.LocalDateTime.now() + " 成功修改。如果不是您本人操作，请立即联系管理员。");
                }
            });

            return ResponseEntity.ok(Map.of("message", "密码修改成功。"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 根据关键字搜索用户
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
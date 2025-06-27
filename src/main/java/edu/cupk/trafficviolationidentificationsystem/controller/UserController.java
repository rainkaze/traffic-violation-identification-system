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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @Autowired
    private NotificationSettingMapper notificationSettingMapper;

    @Autowired
    private EmailService emailService;


    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        Optional<User> optionalUser = userMapper.findByUsername(username);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(new UserDto(optionalUser.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto updatedUser = userService.updateProfile(username, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, passwordChangeDto);


//           对设置了密码修改发通知的人   成功后发送邮件给用户
            Optional<User> byUsername = userMapper.findByUsername(username);
            Integer userId = byUsername.get().getUserId();
            List<Integer> userIds =notificationSettingMapper.getUserIdsByTypeKey("password_change_alert");

            if (userIds.contains(userId)) {
                String email =userMapper.findEmailByUserId(userId);
                emailService.sendSimpleMessage(email, "密码修改成功", "密码修改成功。");
            }


            return ResponseEntity.ok(Map.of("message", "密码修改成功。"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }



//    获取用户id的方法 因为token里没存id 我后续都是调用这个来获取对应用户id的
    @GetMapping("/getUserId")
    public Integer getUserId() {
//        System.out.println("hhhhhhhhh");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username="";
        if (principal instanceof UserDetails userDetails) {
//            System.out.println( userDetails.getUsername());
            username= userDetails.getUsername();
        } else {
            System.out.println(principal.toString());
        }

        Optional<User> byUsername = userMapper.findByUsername(username);
        Integer userId=byUsername.get().getUserId();
//        System.out.println(userId);
        return userId;
    }




//    这个是发布任务功能的搜索框函数 查找用户的
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam(required = false) String keyword) {
        System.out.println("搜索关键字：" + keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return userMapper.getAllUsers(); // 你需要写这个方法
        }
        List<User> a=userMapper.searchUsersByKeyword(keyword);
        System.out.println(a);
        return a;
    }

}
package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PasswordChangeDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ProfileUpdateDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new UserDto(user));
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
            return ResponseEntity.ok(Map.of("message", "密码修改成功。"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }


    @GetMapping("/getUserId")
    public Integer getUserId() {
        System.out.println("hhhhhhhhh");
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
        System.out.println(userId);
        return userId;
    }

}
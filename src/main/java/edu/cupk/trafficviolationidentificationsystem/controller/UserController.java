package edu.cupk.trafficviolationidentificationsystem.controller;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        // 查询该用户的辖区列表
        List<Integer> districts = userMapper.findDistrictIdsByUserId(user.getUserId());
        // 辖区转为 String
        List<String> districtStrings = new ArrayList<>();
        for (Integer districtId : districts) {
            districtStrings.add(String.valueOf(districtId));
        }
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setDistricts(districtStrings);
        System.out.println("districts: " + districtStrings);
        // 返回刚才设置了辖区的 dto 对象，而不是新建的 UserDto
        return ResponseEntity.ok(dto);
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
}
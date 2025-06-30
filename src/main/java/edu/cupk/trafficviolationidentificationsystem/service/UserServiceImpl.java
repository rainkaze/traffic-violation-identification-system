package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PasswordChangeDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ProfileUpdateDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto; // 确认导入

import java.util.List;
import java.util.stream.Collectors; // 确认导入
import java.util.Collections; // 确认导入
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto updateProfile(String username, ProfileUpdateDto profileUpdateDto) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查邮箱是否被更改为已存在的邮箱
        if (!user.getEmail().equals(profileUpdateDto.getEmail())) {
            if (userMapper.existsByEmail(profileUpdateDto.getEmail())) {
                throw new RuntimeException("该邮箱已被其他用户注册");
            }
        }

        user.setFullName(profileUpdateDto.getFullName());
        user.setEmail(profileUpdateDto.getEmail());

        userMapper.updateUser(user);
        UserDto updatedDto = new UserDto(user);
        updatedDto.setDistricts(userMapper.findDistrictsByUserId(user.getUserId())); // 顺便把辖区信息也带上
        return updatedDto;
       // return new UserDto(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, PasswordChangeDto passwordChangeDto) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证当前密码
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("当前密码不正确");
        }

        // 加密并设置新密码
        user.setPasswordHash(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userMapper.updateUserPassword(user.getUserId(), user.getPasswordHash());
    }
    @Override
    public PageResultDto<UserDto> getUsers(int page, int size, String keyword) {
        long totalItems = userMapper.countAll(keyword);
        if (totalItems == 0) {
            return new PageResultDto<>(Collections.emptyList(), 0, 0, page);
        }

        int offset = page * size;
        List<User> users = userMapper.findAllByPage(keyword, size, offset);
        List<UserDto> userDtos = users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());

        long totalPages = (long) Math.ceil((double) totalItems / size);

        return new PageResultDto<>(userDtos, totalItems, (int) totalPages, page);
    }
}
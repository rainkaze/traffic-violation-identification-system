package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    public AdminServiceImpl(UserMapper userMapper, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.findAll().stream().map(UserDto::new).collect(Collectors.toList());
    }

    @Override
    public void approveUser(Integer userId) {
        User user = userMapper.findAll().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 生成一个随机密码
        String randomPassword = RandomStringUtils.randomAlphanumeric(10);
        userMapper.updateUserPassword(userId, passwordEncoder.encode(randomPassword));
        userMapper.updateUserStatus(userId, "APPROVED");

        // 发送批准邮件和随机密码
        String subject = "您的账户已批准 - 交通违法管理系统";
        String text = String.format("您好, %s！\n\n您的账户已被管理员批准。您可以使用以下初始密码登录系统，并请尽快修改密码。\n\n用户名: %s\n初始密码: %s\n\n祝好,\n系统管理员",
                user.getFullName(), user.getUsername(), randomPassword);
        emailService.sendSimpleMessage(user.getEmail(), subject, text);
    }

    @Override
    public void rejectUser(Integer userId) {
        userMapper.updateUserStatus(userId, "REJECTED");
    }
}
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserForAssignmentDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
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
        List<User> users = userMapper.findAll();
        // 遍历所有用户，为每个用户DTO填充其辖区列表
        return users.stream().map(user -> {
            UserDto dto = new UserDto(user);
            dto.setDistricts(userMapper.findDistrictsByUserId(user.getUserId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void approveUser(Integer userId) {
        User user = userMapper.findById(userId)
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

    @Override
    @Transactional
    public UserDto createUser(UserUpsertDto userUpsertDto) {
        if (userMapper.existsByUsername(userUpsertDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.existsByEmail(userUpsertDto.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(userUpsertDto.getUsername());
        user.setFullName(userUpsertDto.getFullName());
        user.setEmail(userUpsertDto.getEmail());
        user.setRank(userUpsertDto.getRank());
        user.setRegistrationStatus("APPROVED");

        String randomPassword = RandomStringUtils.randomAlphanumeric(12);
        user.setPasswordHash(passwordEncoder.encode(randomPassword));

        userMapper.insertUser(user);

        String subject = "欢迎加入交通违法管理系统";
        String text = String.format("您好, %s！\n\n您的账户已由管理员创建。\n\n用户名: %s\n初始密码: %s\n\n请尽快登录并修改您的密码。\n\n祝好,\n系统管理员",
                user.getFullName(), user.getUsername(), randomPassword);
        emailService.sendSimpleMessage(user.getEmail(), subject, text);

        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userId, UserUpsertDto userUpsertDto) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        userMapper.findByEmail(userUpsertDto.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getUserId().equals(userId)) {
                throw new RuntimeException("该邮箱已被其他用户注册");
            }
        });

        user.setFullName(userUpsertDto.getFullName());
        user.setEmail(userUpsertDto.getEmail());
        user.setRank(userUpsertDto.getRank());

        userMapper.updateUser(user);

        return new UserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("不能删除超级管理员账户！");
        }
        userMapper.deleteUserById(userId);
    }

    @Override
    @Transactional
    public void updateUserDistricts(Integer userId, List<Integer> districtIds) {
        // 先删除该用户的所有旧辖区关联
        userMapper.deleteDistrictsByUserId(userId);
        // 如果传入了新的辖区ID列表，则逐一插入新关联
        if (districtIds != null && !districtIds.isEmpty()) {
            for (Integer districtId : districtIds) {
                userMapper.insertUserDistrict(userId, districtId);
            }
        }
    }

    @Override
    public List<UserForAssignmentDto> getUsersForAssignment(Integer districtId) {
        List<User> users = userMapper.findUsersForAssignment(districtId);
        return users.stream().map(user -> {
            UserForAssignmentDto dto = new UserForAssignmentDto();
            dto.setUserId(user.getUserId());
            dto.setFullName(user.getFullName());
            dto.setRank(user.getRank());
            dto.setDistricts(userMapper.findDistrictsByUserId(user.getUserId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
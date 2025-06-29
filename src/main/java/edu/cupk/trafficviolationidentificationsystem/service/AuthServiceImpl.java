// rainkaze/traffic-violation-identification-system/traffic-violation-identification-system-feature-login/src/main/java/edu/cupk/trafficviolationidentificationsystem/service/AuthServiceImpl.java
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.JwtAuthResponseDto;
import edu.cupk.trafficviolationidentificationsystem.dto.LoginDto;
import edu.cupk.trafficviolationidentificationsystem.dto.RegisterDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.security.JwtTokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService; // 注入EmailService

    // 用于存储验证码的内存缓存
    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> codeTimestamps = new ConcurrentHashMap<>();
    private static final long CODE_EXPIRATION_MINUTES = 10;
    private final RedisTemplate<String, Object> redisTemplate;
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, EmailService emailService, RedisTemplate<String, Object> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService; // 初始化EmailService
        this.redisTemplate = redisTemplate; // 注入 RedisTemplate
    }

    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userMapper.findByUsername(loginDto.getUsername()).orElseThrow();

        return new JwtAuthResponseDto(token, user);
    }

    @Override
    public void sendVerificationCode(String email) {
        if (userMapper.existsByEmail(email)) {
            throw new RuntimeException("该邮箱已被注册！");
        }
        String code = RandomStringUtils.randomNumeric(6);
        verificationCodes.put(email, code);
        codeTimestamps.put(email, System.currentTimeMillis());

        String subject = "交通违法管理系统 - 注册验证码";
        String text = String.format("您好！\n\n您的注册验证码是：%s\n\n该验证码将在 %d 分钟后失效。请勿泄露给他人。\n\n交通违法管理系统", code, CODE_EXPIRATION_MINUTES);
        emailService.sendSimpleMessage(email, subject, text);
    }


    @Override
    public String register(RegisterDto registerDto) {
        // 1. 验证邮箱和用户名是否已存在
        if (userMapper.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("用户名已被占用！");
        }
        if (userMapper.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("该邮箱已被注册！");
        }

        // 2. 验证码校验
        String storedCode = verificationCodes.get(registerDto.getEmail());
        Long timestamp = codeTimestamps.get(registerDto.getEmail());

        if (storedCode == null || timestamp == null) {
            throw new RuntimeException("请先获取验证码！");
        }
        if ((System.currentTimeMillis() - timestamp) > TimeUnit.MINUTES.toMillis(CODE_EXPIRATION_MINUTES)) {
            throw new RuntimeException("验证码已过期，请重新获取！");
        }
        if (!storedCode.equals(registerDto.getVerificationCode())) {
            throw new RuntimeException("验证码不正确！");
        }


        User user = new User();
        user.setFullName(registerDto.getFullName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        // 密码字段设置为一个不可登录的占位符
        user.setPasswordHash(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(32)));
        user.setRank(registerDto.getRank());
        user.setRegistrationStatus("PENDING"); // 状态为待审核

        userMapper.save(user);

        // 注册成功后清除验证码
        verificationCodes.remove(registerDto.getEmail());
        codeTimestamps.remove(registerDto.getEmail());


        return "申请已成功提交！请等待管理员审核。";
    }
    // 新增 logout 方法
    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            if (jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsername(jwt);
                long expirationTime = jwtTokenProvider.getExpirationDateFromToken(jwt).getTime();
                long currentTime = System.currentTimeMillis();
                long ttl = expirationTime - currentTime;
                if (ttl > 0) {
                    // 将 token 存入 Redis 黑名单，并设置过期时间
                    redisTemplate.opsForValue().set("blacklist:" + jwt, username, ttl, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
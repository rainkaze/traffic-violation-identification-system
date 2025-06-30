package edu.cupk.trafficviolationidentificationsystem.security;

import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务。
 * <p>
 * 实现了 Spring Security 的 {@link UserDetailsService} 接口，
 * 负责在用户认证过程中，根据用户名从数据库中加载用户的详细信息（包括密码和权限）。
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserMapper userMapper;

    /**
     * 构造函数，注入 UserMapper。
     * @param userMapper 用于数据库用户查询的 MyBatis Mapper 接口。
     */
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据用户名加载用户。
     * <p>
     * 这是认证流程的核心方法。Spring Security 会在用户尝试登录时调用此方法。
     * </p>
     * @param username 用户在登录时提供的用户名。
     * @return 一个实现了 {@link UserDetails} 接口的对象 (在本项目中是 User 对象)。
     * @throws UsernameNotFoundException 如果数据库中不存在该用户。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("正在为用户 '{}' 加载用户详情...", username);
        return userMapper.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("加载用户详情失败：在数据库中未找到用户 '{}'。", username);
                    return new UsernameNotFoundException("未找到用户: " + username);
                });
    }
}
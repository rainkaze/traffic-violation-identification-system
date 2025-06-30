package edu.cupk.trafficviolationidentificationsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器。
 * <p>
 * 继承自 {@link OncePerRequestFilter}，确保每个请求只执行一次此过滤器。
 * 它的核心职责是：
 * 1. 从 HTTP 请求头中提取 JWT。
 * 2. 检查 JWT 是否在 Redis 黑名单中（用于实现登出功能）。
 * 3. 如果 JWT 有效且不在黑名单中，则解析它以获取用户名。
 * 4. 加载用户信息并创建一个认证令牌。
 * 5. 将认证令牌设置到 Spring Security 的安全上下文中 (SecurityContextHolder)。
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 过滤器的核心处理逻辑。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求中获取 JWT
        String token = getTokenFromRequest(request);

        // 如果没有 token，直接放行到下一个过滤器
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 检查 token 是否在 Redis 黑名单中
        if (redisTemplate.hasKey("blacklist:" + token)) {
            log.warn("检测到已列入黑名单的 Token。拒绝访问。");
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 验证 token
        if (jwtTokenProvider.validateToken(token)) {
            // 4. 从 token 中获取用户名
            String username = jwtTokenProvider.getUsername(token);
            log.trace("Token 验证通过，用户名为: {}", username);

            // 5. 加载用户详情并创建认证令牌
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. 将认证令牌设置到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("用户 '{}' 的认证信息已成功设置到 SecurityContext。", username);
        } else {
            log.warn("请求中的 Token 无效或已过期。");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从 HttpServletRequest 的 "Authorization" 请求头中提取 Bearer Token。
     *
     * @param request HTTP 请求。
     * @return 提取出的 JWT 字符串，如果不存在或格式不正确则返回 null。
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
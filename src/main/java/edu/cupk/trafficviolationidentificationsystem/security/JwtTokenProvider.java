package edu.cupk.trafficviolationidentificationsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token 提供者。
 * <p>
 * 封装了所有与 JSON Web Token (JWT) 相关的操作，包括：
 * <ul>
 * <li>生成 Token</li>
 * <li>从 Token 中解析信息 (如用户名、过期时间)</li>
 * <li>验证 Token 的有效性 (签名、是否过期等)</li>
 * </ul>
 * </p>
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationDate;

    /**
     * 根据用户的认证信息生成一个新的 JWT。
     *
     * @param authentication 包含用户主体信息的 Spring Security Authentication 对象。
     * @return 生成的 JWT 字符串。
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key()) // 使用基于 HMAC-SHA 算法的密钥进行签名
                .compact();
        log.info("为用户 '{}' 生成了新的 Token，有效期至: {}", username, expireDate);
        return token;
    }

    /**
     * 从 application.properties 中的 jwtSecret (Base64编码) 生成一个 SecretKey 对象。
     * <p>
     * 这是一个安全的做法，用于对 JWT 进行签名和验证。
     * </p>
     * @return 用于 HMAC-SHA 算法的 SecretKey。
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * 从 Token 字符串中解析出 Claims (负载)。
     *
     * @param token JWT 字符串。
     * @return Claims 对象，包含了 Token 的所有声明。
     */
    private Claims parseClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户名 (即 subject 声明)。
     *
     * @param token JWT 字符串。
     * @return 用户名。
     */
    public String getUsername(String token) {
        return parseClaimsJws(token).getSubject();
    }

    /**
     * 从 Token 中获取过期日期。
     *
     * @param token JWT 字符串。
     * @return 过期日期。
     */
    public Date getExpirationDateFromToken(String token) {
        return parseClaimsJws(token).getExpiration();
    }

    /**
     * 验证给定的 Token 是否有效。
     * <p>
     * 有效性检查包括：签名是否正确、Token 是否在有效期内、格式是否正确。
     * </p>
     * @param token 要验证的 JWT 字符串。
     * @return 如果 Token 有效则返回 true，否则返回 false。
     */
    public boolean validateToken(String token) {
        try {
            // 只要能成功解析，就说明签名和结构是正确的
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 验证失败: Token 已过期。");
        } catch (MalformedJwtException e) {
            log.warn("Token 验证失败: Token 格式不正确。");
        } catch (SignatureException e) {
            log.warn("Token 验证失败: 签名无效。");
        } catch (IllegalArgumentException e) {
            log.warn("Token 验证失败: Token 字符串为空或格式非法。");
        } catch (Exception e) {
            log.error("Token 验证时发生未知错误。", e);
        }
        return false;
    }
}
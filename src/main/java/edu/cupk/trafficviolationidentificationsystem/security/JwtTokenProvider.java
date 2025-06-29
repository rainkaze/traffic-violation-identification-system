package edu.cupk.trafficviolationidentificationsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationDate;

    // 生成 Token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key()) // 使用现代的 signWith(Key) 方法
                .compact();
    }

    // 从 jwtSecret 生成 SecretKey
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 从 Token 中解析出 Claims (有效载荷)
    private Claims parseClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return parseClaimsJws(token).getSubject();
    }

    /**
     * 从 Token 中获取过期日期
     */
    public Date getExpirationDateFromToken(String token) {
        return parseClaimsJws(token).getExpiration();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 解析成功即表示 token 有效
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 可以根据需要记录日志，例如 token 过期、格式错误等
            // e.g., io.jsonwebtoken.ExpiredJwtException
            return false;
        }
    }
}
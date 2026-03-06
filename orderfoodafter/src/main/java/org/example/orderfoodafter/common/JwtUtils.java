package org.example.orderfoodafter.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具类
 */
@Component
public class JwtUtils {

    // 从配置文件读取JWT密钥（建议在application.yml中配置）
    private String secret="defaultSecretKeyMustBeLongEnoughToMeetHmacRequirements";

    // 从配置文件读取令牌过期时间（单位：毫秒，建议在application.yml中配置）
    private long expiration=86400000;

    /**
     * 生成JWT令牌
     * @param subject 主题（通常是用户名或用户ID）
     * @param claims 自定义载荷（如角色、权限等）
     * @return 生成的令牌
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                // 添加自定义载荷
                .setClaims(claims)
                // 设置主题
                .setSubject(subject)
                // 设置签发时间
                .setIssuedAt(now)
                // 设置过期时间
                .setExpiration(expirationDate)
                // 设置签名算法和密钥
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                // 压缩令牌
                .compact();
    }

    /**
     * 生成JWT令牌（无自定义载荷）
     * @param subject 主题（通常是用户名或用户ID）
     * @return 生成的令牌
     */
    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    /**
     * 从令牌中获取主题（用户名/用户ID）
     * @param token JWT令牌
     * @return 主题信息
     */
    public String getSubject(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public String extractUserId(String token) {
        return getSubject(token);
    }

    /**
     * 从令牌中获取指定的载荷信息
     * @param token JWT令牌
     * @param claimResolver 解析函数
     * @param <T> 载荷类型
     * @return 解析后的载荷值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @param subject 预期的主题（用于验证令牌所属主体）
     * @return 令牌是否有效
     */
    public boolean validateToken(String token, String subject) {
        String tokenSubject = getSubject(token);
        return (tokenSubject.equals(subject) && !isTokenExpired(token));
    }

    /**
     * 验证令牌是否有效（仅验证令牌本身，不验证主体）
     * @param token JWT令牌
     * @return 令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查令牌是否已过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    /**
     * 获取令牌的剩余过期时间（毫秒）
     * @param token JWT令牌
     * @return 剩余时间（毫秒），已过期则返回0
     */
    public long getRemainingExpirationTime(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        long remaining = expiration.getTime() - new Date().getTime();
        return Math.max(remaining, 0);
    }

    /**
     * 从令牌中获取所有载荷信息
     * @param token JWT令牌
     * @return 载荷信息
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new SecurityException("JWT签名验证失败", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("无效的JWT令牌格式", e);
        } catch (ExpiredJwtException e) {
            throw new SecurityException("登录过期，请重新登录", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("不支持的JWT令牌", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT令牌为空", e);
        }
    }

    /**
     * 生成签名密钥（基于配置的secret字符串）
     * @return 密钥对象
     */
    private SecretKey getSecretKey() {
        // 使用HS256算法需要至少256位（32字节）的密钥
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

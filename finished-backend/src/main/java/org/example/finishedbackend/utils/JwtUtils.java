package org.example.finishedbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String value;

    @Autowired
    StringRedisTemplate redisTemplate;

    // 创建Jwt令牌
    public String createToken(UserDetails user, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(this.value);
        Date date = expireDate();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }


    public boolean deleteToken(String headerToken) {
       try {
           String token = this.subToken(headerToken);
           DecodedJWT jwt = resolveToken(token);
           String id = jwt.getId();
           Date expiresAt = jwt.getExpiresAt();
           if (invalidToken(id)) return false;
           Date date = new Date();
           long expire = Math.max(expiresAt.getTime() - date.getTime(), 0);
           redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + id, "", expire, TimeUnit.MILLISECONDS);
           return true;
       } catch (JWTVerificationException | NullPointerException e) {
           return false;
       }
    }

    public DecodedJWT resolveToken(String token) {
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(this.value);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            if (invalidToken(verify.getId())) return null;
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt) ? null : verify;
        }catch (JWTVerificationException e) {
            return null;
        }
    }

    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("username").toString())
                .password("")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Date expireDate(){
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 对当前时间进行 * 1天的操作
        calendar.add(Calendar.MINUTE, Const.JWT_EXPIRE_TIME);
        // 返回过期时间
        return calendar.getTime();
    }

    public boolean invalidToken(String uuid) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    public int toId(DecodedJWT jwt) {
        return jwt.getClaim("id").asInt();
    }

    private String subToken(String headerToken) {
        return headerToken == null || !headerToken.startsWith("Bearer ") ? null : headerToken.substring(7);
    }
}

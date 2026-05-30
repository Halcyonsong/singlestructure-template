package io.github.singlestructuretemplate.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static String KEY;
    private static Long EXPIRE_MILLIS;

    // 利用非静态的 setter 方法，让 Spring 把 yml 里的值注入进来，然后赋给静态变量
    @Value("${jwt.secret}")
    public void setKey(String secret) {
        JwtUtil.KEY = secret;
    }

    @Value("${jwt.expire-hours}")
    public void setExpireMillis(Integer expireHours) {
        JwtUtil.EXPIRE_MILLIS = expireHours * 60L * 60L * 1000L;
    }

    // 接收业务数据，生成 token 并返回
    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS))
                .sign(Algorithm.HMAC256(KEY));
    }

    // 接收 token，验证 token，并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}

package io.github.singlestructuretemplate.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {


    private final String KEY;
    private final long EXPIRE_MILLIS;
    private static final String CLAIMS_KEY = "claims";

    // 通过构造器注入
    private JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-hours}") long expireHours) {
        this.KEY = secret;
        this.EXPIRE_MILLIS = expireHours * 60 * 60 * 1000L;
    }

    public String genToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim(CLAIMS_KEY, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS))
                .sign(Algorithm.HMAC256(KEY));
    }

    public Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim(CLAIMS_KEY)
                .asMap();
    }
}

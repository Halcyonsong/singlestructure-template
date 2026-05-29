package io.github.singlestructuretemplate.interceptors;

import io.github.singlestructuretemplate.utils.JwtUtil;
import io.github.singlestructuretemplate.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @Override// 核心方法：在 Controller 的方法执行之前运行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 Token
        String token = request.getHeader("Authorization");
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();// 获取 Redis 操作对象
            String redisToken = operations.get(token);// 根据 Token 去 Redis 查是否存在
            if (redisToken==null){
                throw new RuntimeException();
            }

            Map<String,Object> claims = JwtUtil.parseToken(token);// 解析 Token 获取用户信息 (claims)
            ThreadLocalUtil.set(claims);
            return true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception{
        ThreadLocalUtil.remove();
    }
}

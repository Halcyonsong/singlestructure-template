package io.github.singlestructuretemplate.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.singlestructuretemplate.enums.ResultCodeEnum;
import io.github.singlestructuretemplate.exception.BusinessException;
import io.github.singlestructuretemplate.pojo.Result;
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
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Override// 核心方法：在 Controller 的方法执行之前运行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            returnAuthError(response, "未携带Token，请先登录");
            return false;
        }
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (redisToken == null) {
                returnAuthError(response, "请先登录");
                return false;
            }
            Map<String, Object> claims = jwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            returnAuthError(response, "无效的Token，请重新登录");
            return false;
        }
    }

//    后续可以从token里面获取信息而不用每次去mysql数据库查
//    Map<String, Object> map = ThreadLocalUtil.get();
//    String username = (String) map.get("username");

    private void returnAuthError(HttpServletResponse response, String message) throws Exception {
        // 设置响应状态码为 401
        response.setStatus(401);
        // 必须要设置字符集和内容类型，否则前端拿到的是乱码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        // 组装自定义 Result 对象
        Result<?> errorResult = Result.error(ResultCodeEnum.UNAUTHORIZED.getCode(), message);
        // 使用 ObjectMapper 将对象转为 JSON 字符串，写入响应体
        String json = objectMapper.writeValueAsString(errorResult);
        response.getWriter().print(json);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception{
        ThreadLocalUtil.remove();
    }
}

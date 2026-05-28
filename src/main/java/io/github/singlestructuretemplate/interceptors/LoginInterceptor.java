package io.github.singlestructuretemplate.interceptors;

import io.github.singlestructuretemplate.utils.JwtUtil;
import io.github.singlestructuretemplate.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

//    注：注册token一般在登录逻辑中完成，代码示例：
//    @Autowired //获取Redis容器
//    private StringRedisTemplate stringRedisTemplate;
//    注册一般用于验证登录通过后，实为存入redis数据库， Key 是 token，Value 是用户信息
//    User loginUser = userservice.findByUserName(username);//先用查询逻辑获取用户信息
//    Map<String,Object> claims = new HashMap<>();//创建一个 Map 集合，存入登录成功的用户关键信息
//    claims.put("id",loginUser.getId());
//    claims.put("username",loginUser.getUsername());
//    String token = JwtUtil.genToken(claims);//调用工具类生成加密字符串（Token）
//
//    ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//从 Redis 模板中获取专门操作字符串（String）类型的对象
//    operations.set(token,token,1, TimeUnit.HOURS);//实际存入,key为token,value为token（占位即可，可以换），设置有效期1小时
//
//    后续可以从token里面获取信息而不用每次去mysql数据库查
//    Map<String, Object> map = ThreadLocalUtil.get();
//    String username = (String) map.get("username");



    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception{
        ThreadLocalUtil.remove();
    }
}

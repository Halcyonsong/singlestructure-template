package io.github.singlestructuretemplate.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect      // 告诉 Spring 这是一个切面类
@Component   // 交给 Spring 容器管理
@RequiredArgsConstructor
public class WebLogAspect {

    private final ObjectMapper objectMapper;

    // 定义切入点：拦截 controller 包下的所有类的所有方法！
    // 【注意】把这里的 io.github.singlestructuretemplate 换成你实际的包名
    @Pointcut("execution(public * io.github.singlestructuretemplate.controller..*.*(..))")
    public void webLog() {
    }

    // 环绕通知：在方法执行前和执行后都会走这里
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 1. 获取当前 HTTP 请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 2. 打印请求前置日志
        log.info("================== Request Start ==================");
        log.info("URL            : {}", request.getRequestURL().toString());
        log.info("HTTP Method    : {}", request.getMethod());
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        log.info("IP             : {}", request.getRemoteAddr());

        // 过滤无法序列化的参数对象，防止抛出 JsonProcessingException
        Object[] args = joinPoint.getArgs();
        List<Object> logArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof MultipartFile) {
                // 如果是特殊对象，只打印类型名称，不序列化它的内容
                logArgs.add(arg.getClass().getSimpleName());
            } else {
                logArgs.add(arg);
            }
        }
        // 打印过滤后的安全参数
        try {
            log.info("Request Args   : {}", objectMapper.writeValueAsString(logArgs));
        } catch (Exception e) {
            log.warn("Request Args   : [参数无法序列化为JSON]");
        }

        // 3. 【极其重要】放行，去执行你真正 Controller 里的业务代码
        Object result = joinPoint.proceed();

        // 打印响应结果（如果结果为 null，防止序列化报错）
        try {
            log.info("Response Result: {}", result == null ? "null" : objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.warn("Response Result: [结果无法序列化为JSON]");
        }

        log.info("Time Consuming : {} ms", System.currentTimeMillis() - startTime);
        log.info("================== Request End ====================");


        // 5. 将结果返回给前端
        return result;
    }
}

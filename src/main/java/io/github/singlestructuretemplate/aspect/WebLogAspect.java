package io.github.singlestructuretemplate.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect      // 告诉 Spring 这是一个切面类
@Component   // 交给 Spring 容器管理
public class WebLogAspect {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

        // 将参数转成 JSON 打印（使用了 Jackson，Spring自带）
        log.info("Request Args   : {}", MAPPER.writeValueAsString(joinPoint.getArgs()));

        // 3. 【极其重要】放行，去执行你真正 Controller 里的业务代码
        Object result = joinPoint.proceed();

        // 4. 打印请求后置日志（包含耗时）
        log.info("Response Result: {}", MAPPER.writeValueAsString(result));
        log.info("Time Consuming : {} ms", System.currentTimeMillis() - startTime);
        log.info("================== Request End ====================");

        // 5. 将结果返回给前端
        return result;
    }
}

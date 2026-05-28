package io.github.singlestructuretemplate.exception;


import io.github.singlestructuretemplate.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j // 引入 Lombok 的日志注解
@RestControllerAdvice //@ControllerAdvice + @ResponseBody，任何一个 Controller 的异常都会到这里来处理
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)// Exception 是所有异常的父类，任何报错都会被这个方法拦截
    public Result<?> handleException(Exception e) {
        // 使用标准的日志输出，记录完整的堆栈信息
        log.error("系统发生未捕获异常: {}",e.getMessage(), e);
        // 可能会暴露内部结构，待优化
        String message = StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "服务器开小差了，请稍后再试";
        return Result.error(message);
    }
}

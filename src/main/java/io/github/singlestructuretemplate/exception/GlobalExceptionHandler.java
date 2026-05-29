package io.github.singlestructuretemplate.exception;

import io.github.singlestructuretemplate.enums.ResultCodeEnum;
import io.github.singlestructuretemplate.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidationException(Exception e) {
        String msg = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex) {
            FieldError fieldError = ex.getBindingResult().getFieldError();
            if (fieldError != null) msg = fieldError.getDefaultMessage();
        } else if (e instanceof BindException ex) {
            FieldError fieldError = ex.getBindingResult().getFieldError();
            if (fieldError != null) msg = fieldError.getDefaultMessage();
        }
        log.warn("参数校验异常: {}", msg);
        return Result.error(ResultCodeEnum.PARAM_ERROR.getCode(), msg);
    }


    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务执行异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统发生未捕获异常", e);
        return Result.error(ResultCodeEnum.SYSTEM_ERROR);
    }
}

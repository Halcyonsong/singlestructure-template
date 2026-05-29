package io.github.singlestructuretemplate.pojo;

import io.github.singlestructuretemplate.enums.ResultCodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private long timestamp; // 新增：响应时间戳

    // 私有构建方法
    private static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis());
    }


    public static <T> Result<T> success() {
        return build(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return build(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }



    // 默认服务器错误 500
    public static <T> Result<T> error(String message) {
        return build(ResultCodeEnum.SYSTEM_ERROR.getCode(), message, null);
    }

    // 自定义状态码和信息
    public static <T> Result<T> error(Integer code, String message) {
        return build(code, message, null);
    }

    // 直接传入枚举
    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum) {
        return build(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }
}

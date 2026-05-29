package io.github.singlestructuretemplate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    // 相当于创建了五种 ResultCodeEnum 类型的对象
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "没有权限访问该资源"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统异常，请稍后再试");


    private final Integer code;
    private final String message;
}

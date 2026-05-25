package io.github.singlestructuretemplate.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统一 API 响应结果封装类
// @param <T> 泛型，代表真正要返回的数据类型
@Data
// 生成无参构造，并设为 private
@NoArgsConstructor(access = AccessLevel.PRIVATE)
// 生成全参构造，并设为 private
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    // 业务状态码 (比如 200 代表成功，400 代表参数错误，500 代表服务器异常)
    private Integer code;
    // 提示信息
    private String message;
    // 实际的数据
    private T data;

//    // 如果想在里面写业务代码可以手动，默认的话就用注解生成
//    // 私有化构造方法，强制通过后面的静态方法来创建对象
//    private Result() {}
//
//    private Result(Integer code, String message, T data) {
//        this.code = code;
//        this.message = message;
//        this.data = data;
//    }

    // 快捷静态方法：成功时调用
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }


    // 快捷静态方法：失败时调用
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}

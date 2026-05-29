package io.github.singlestructuretemplate.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.singlestructuretemplate.common.CreateGroup;
import io.github.singlestructuretemplate.common.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
// 默认 ignoreUnknown = true 忽略未知字段，改为 false，如果有不认识的字段，就抛异常
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserDTO {
    @NotNull(message = "修改操作 ID 不能为空", groups = UpdateGroup.class)// 分组校验
    private long id;
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 16, message = "用户名长度必须在5-16位之间")
    private String name;

    @NotBlank(message = "密码不能为空", groups = CreateGroup.class)
    @Size(min = 5, max = 16, message = "密码长度必须在5-16位之间")
    private String password;

    private Integer age;
}

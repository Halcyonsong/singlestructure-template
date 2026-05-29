package io.github.singlestructuretemplate.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExampleDTO {
    private int id;
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 16, message = "用户名长度必须在5-16位之间")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 16, message = "密码长度必须在5-16位之间")
    private String password;

    private Integer age;
}

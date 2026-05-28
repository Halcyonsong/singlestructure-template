package io.github.singlestructuretemplate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * 【数据库实体类 - Entity】
 * 定位：它的字段必须和数据库表中的列一模一样！
 * 范围：只能在当前微服务(submodule-1)的 Mapper 层和 Service 层内部流转。
 * 架构师注：绝对不能把它放到 Common 公共模块中，也尽量不要直接返回给前端，防止数据库表结构泄露。
 * 这里不需要写 Validation 参数校验，因为它是由我们自己代码生成的，或者是从数据库查出来的，绝对安全。
 */
@Data /*一键生成Javabean*/
public class ExampleEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
}

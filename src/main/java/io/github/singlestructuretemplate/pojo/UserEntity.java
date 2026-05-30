package io.github.singlestructuretemplate.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("example_entity") //表名对不上时用映射
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    @JsonIgnore
    private String password;
    private Integer age;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//更新时间
}

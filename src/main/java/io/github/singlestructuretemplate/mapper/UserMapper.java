package io.github.singlestructuretemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.singlestructuretemplate.pojo.UserEntity;
import org.apache.ibatis.annotations.*;


@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}

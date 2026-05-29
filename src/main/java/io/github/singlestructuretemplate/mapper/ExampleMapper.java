package io.github.singlestructuretemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import org.apache.ibatis.annotations.*;


@Mapper
public interface ExampleMapper extends BaseMapper<ExampleEntity> {

}

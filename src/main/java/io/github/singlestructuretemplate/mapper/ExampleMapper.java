package io.github.singlestructuretemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExampleMapper extends BaseMapper<ExampleEntity> {

}

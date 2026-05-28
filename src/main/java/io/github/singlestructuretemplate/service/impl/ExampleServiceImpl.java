package io.github.singlestructuretemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.singlestructuretemplate.mapper.ExampleMapper;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.pojo.PageResult;
import io.github.singlestructuretemplate.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {
    private final ExampleMapper exampleMapper;

    @Override
    public int addExample(ExampleEntity example) {
        return exampleMapper.insert(example);
    }
    @Override
    public int removeById(Long id) {
        return exampleMapper.deleteById(id);
    }
    @Override
    public int modifyExample(ExampleEntity example) {
        return exampleMapper.updateById(example);
    } // 没传的字段不改
    @Override
    public ExampleEntity getById(Long id) {
        return exampleMapper.selectById(id);
    }
    @Override
    public List<ExampleEntity> getAll() {
        return exampleMapper.selectList(null);
    }

    @Override
    public PageResult<ExampleEntity> getByPage(long pageCurrent,long pageSize) {
        IPage<ExampleEntity> page = new Page(pageCurrent,pageSize);
        exampleMapper.selectPage(page,null);
        return PageResult.Setter(page);
    }

    @Override
    public List<ExampleEntity> getRequired(Integer minAge,Integer maxAge) {
        LambdaQueryWrapper<ExampleEntity> lqw = new LambdaQueryWrapper<>();
        lqw.ge(null != minAge,ExampleEntity::getAge,minAge);
        lqw.le(null != maxAge,ExampleEntity::getAge,maxAge);
        List<ExampleEntity> lists = exampleMapper.selectList(lqw);
        return lists;
    }


}

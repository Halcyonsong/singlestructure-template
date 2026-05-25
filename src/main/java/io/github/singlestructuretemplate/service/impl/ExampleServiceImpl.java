package io.github.singlestructuretemplate.service.impl;

import io.github.singlestructuretemplate.mapper.ExampleMapper;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExampleServiceImpl implements ExampleService {
    // 依赖注入 (DI)：把刚才写的 Mapper 自动注入进来
    @Autowired
    private ExampleMapper exampleMapper;

    @Override //添加
    public int addExample(ExampleEntity example) {
        // 这里可以写业务逻辑，比如判断 example.getName() 是否为空，为空抛出异常
        return exampleMapper.insert(example);
    }
    @Override //删除
    public int removeById(Long id) {
        return exampleMapper.deleteById(id);
    }
    @Override //修改
    public int modifyExample(ExampleEntity example) {
        return exampleMapper.update(example);
    }
    @Override //id查询
    public ExampleEntity getById(Long id) {
        return exampleMapper.selectById(id);
    }
    @Override //查询全部
    public List<ExampleEntity> getAll() {
        return exampleMapper.selectAll();
    }
    @Override //条件查询
    public List<ExampleEntity> getByNameAndAge(String name, Integer age) {
        return exampleMapper.selectByNameAndAge(name, age);
    }
}

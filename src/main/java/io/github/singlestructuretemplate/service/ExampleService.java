package io.github.singlestructuretemplate.service;


import io.github.singlestructuretemplate.pojo.ExampleEntity;

import java.util.List;

public interface ExampleService {
    // 1. 新增
    int addExample(ExampleEntity example);
    // 2. 根据 ID 删除
    int removeById(Long id);
    // 3. 修改
    int modifyExample(ExampleEntity example);
    // 4. 根据 ID 查询
    ExampleEntity getById(Long id);
    // 5. 查询所有
    List<ExampleEntity> getAll();

    // 6. 复杂条件查询
    List<ExampleEntity> getByNameAndAge(String name, Integer age);

}

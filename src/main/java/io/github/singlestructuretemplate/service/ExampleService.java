package io.github.singlestructuretemplate.service;


import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.pojo.PageResult;

import java.util.List;

public interface ExampleService {
    int addExample(ExampleEntity example);
    int removeById(Long id);
    int modifyExample(ExampleEntity example);
    ExampleEntity getById(Long id);
    List<ExampleEntity> getAll();

    PageResult<ExampleEntity> getByPage(long pageCurrent,long pageSize);

    List<ExampleEntity> getRequired(Integer minAge,Integer maxAge);


}

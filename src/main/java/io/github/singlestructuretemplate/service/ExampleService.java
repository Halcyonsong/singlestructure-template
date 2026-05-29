package io.github.singlestructuretemplate.service;


import io.github.singlestructuretemplate.pojo.ExampleDTO;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.pojo.PageResult;
import io.github.singlestructuretemplate.pojo.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface ExampleService {
    void addExample(ExampleDTO exampleDTO);
    void removeById(Long id);
    void modifyExample(ExampleDTO example);
    ExampleEntity getById(Long id);
    List<ExampleEntity> getAll();

    PageResult<ExampleEntity> getByPage(long pageCurrent,long pageSize);

    List<ExampleEntity> getRequired(Integer minAge,Integer maxAge);

    List<ExampleEntity> getPart();

    ExampleEntity getByName(String name);

    void register(ExampleDTO example);

    Result login(ExampleDTO exampleDTO);


}

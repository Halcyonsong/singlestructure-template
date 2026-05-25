package io.github.singlestructuretemplate.controller;

import io.github.singlestructuretemplate.pojo.Result;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//表现层（Controller）：负责接收 HTTP 请求，参数校验，并调用 Service 处理业务
@RestController //@RestController = @Controller + @ResponseBody (代表此类所有接口均返回 JSON 格式数据)
@RequestMapping("/example")
public class ExampleController {
    // 依赖注入业务层 Service
    @Autowired
    private ExampleService exampleService;

    // 1. 新增
    // @RequestBody: 将前端传来的 JSON 字符串反序列化为 Java 对象
    // @Validated: 开启参数校验（实体类里的 @NotNull 等注解才会生效）
    @PostMapping("/add")
    public Result<Void> add(@Validated @RequestBody ExampleEntity entity) {
        exampleService.addExample(entity);
        return Result.success(); // 甚至不用判断 rows，如果出错，全局异常会兜底！
    }

    // 2. 删除
    // @RequestParam: 接收 URL 问号后面的参数，例如 /example/delete?id=1
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        exampleService.removeById(id);
        return Result.success();
    }

    // 3. 修改
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody ExampleEntity entity) {
        exampleService.modifyExample(entity);
        return Result.success();
    }

    // 4.1 根据 ID 查询单条 (注意泛型写法)
    @GetMapping("/get")
    public Result<ExampleEntity> getById(@RequestParam("id") Long id) {
        ExampleEntity data = exampleService.getById(id);
        return Result.success(data);
    }

    // 4.2 查询列表 (注意泛型写法)
    @GetMapping("/list")
    public Result<List<ExampleEntity>> getAll() {
        List<ExampleEntity> list = exampleService.getAll();
        return Result.success(list);
    }
}

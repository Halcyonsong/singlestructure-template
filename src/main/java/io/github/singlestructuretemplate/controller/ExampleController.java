package io.github.singlestructuretemplate.controller;

import io.github.singlestructuretemplate.pojo.ExampleDTO;
import io.github.singlestructuretemplate.pojo.PageResult;
import io.github.singlestructuretemplate.pojo.Result;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.service.ExampleService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController //@RestController = @Controller + @ResponseBody (代表此类所有接口均返回 JSON 格式数据)
@RequestMapping("/example")
@RequiredArgsConstructor
public class ExampleController {


    private final ExampleService exampleService;

    @PostMapping("/add")
    public Result<Void> add(@Validated @RequestBody ExampleDTO exampleDTO) {
        exampleService.addExample(exampleDTO);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        exampleService.removeById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody ExampleDTO exampleDTO) {
        exampleService.modifyExample(exampleDTO);
        return Result.success();
    }

    @GetMapping("/get")
    public Result<ExampleEntity> getById(@RequestParam("id") Long id) {
        ExampleEntity data = exampleService.getById(id);
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<ExampleEntity>> getAll() {
        List<ExampleEntity> list = exampleService.getAll();
        return Result.success(list);
    }

    @GetMapping("/getByPage")
    public Result<PageResult<ExampleEntity>> getByPage(@RequestParam(defaultValue = "1") long pageCurrent,
                                                       @RequestParam(defaultValue = "3") long pageSize){
        PageResult<ExampleEntity> byPage = exampleService.getByPage(pageCurrent, pageSize);
        return Result.success(byPage);
    }

    @GetMapping("/getRequired")
    public Result<List<ExampleEntity>> getRequired(Integer minAge,Integer maxAge){
        List<ExampleEntity> lists = exampleService.getRequired(minAge,maxAge);
        return Result.success(lists);
    }

    @PostMapping("/register")
    public Result register(@RequestBody @Validated ExampleDTO exampleDTO){
        exampleService.register(exampleDTO);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated ExampleDTO exampleDTO){
        return exampleService.login(exampleDTO);
    }

}

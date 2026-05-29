package io.github.singlestructuretemplate.controller;

import io.github.singlestructuretemplate.common.CreateGroup;
import io.github.singlestructuretemplate.common.UpdateGroup;
import io.github.singlestructuretemplate.pojo.*;
import io.github.singlestructuretemplate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //@RestController = @Controller + @ResponseBody (代表此类所有接口均返回 JSON 格式数据)
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Validated(CreateGroup.class) UserDTO userDTO) {
        userService.addUser(userDTO);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Validated(UpdateGroup.class) UserDTO userDTO) {
        userService.modifyUser(userDTO);
        return Result.success();
    }

    @GetMapping("/get")
    public Result<UserEntity> getById(@RequestParam("id") Long id) {
        UserEntity data = userService.getById(id);
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<UserEntity>> getAll() {
        List<UserEntity> list = userService.getAll();
        return Result.success(list);
    }

    @GetMapping("/getByPage")
    public Result<PageResult<UserEntity>> getByPage(@RequestParam(defaultValue = "1") long pageCurrent,
                                                    @RequestParam(defaultValue = "3") long pageSize){
        PageResult<UserEntity> byPage = userService.getByPage(pageCurrent, pageSize);
        return Result.success(byPage);
    }

    @GetMapping("/getRequired")
    public Result<List<UserEntity>> getRequired(Integer minAge, Integer maxAge){
        List<UserEntity> lists = userService.getRequired(minAge,maxAge);
        return Result.success(lists);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated(CreateGroup.class) UserDTO userDTO){
        userService.register(userDTO);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginDTO loginDTO){
        String token = userService.login(loginDTO);
        return Result.success(token);
    }


}

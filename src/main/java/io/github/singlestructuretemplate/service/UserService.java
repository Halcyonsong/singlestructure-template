package io.github.singlestructuretemplate.service;


import io.github.singlestructuretemplate.pojo.LoginDTO;
import io.github.singlestructuretemplate.pojo.UserDTO;
import io.github.singlestructuretemplate.pojo.UserEntity;
import io.github.singlestructuretemplate.pojo.PageResult;

import java.util.List;

public interface UserService {
    void addUser(UserDTO userDTO);
    void removeById(Long id);
    void modifyUser(UserDTO example);
    UserEntity getById(Long id);
    List<UserEntity> getAll();

    PageResult<UserEntity> getByPage(long pageCurrent, long pageSize);

    List<UserEntity> getRequired(Integer minAge, Integer maxAge);

    List<UserEntity> getPart();

    UserEntity getByName(String name);

    void register(UserDTO userDTO);

    String login(LoginDTO loginDTO);


}

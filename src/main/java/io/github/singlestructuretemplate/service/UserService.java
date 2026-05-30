package io.github.singlestructuretemplate.service;


import io.github.singlestructuretemplate.pojo.*;

import java.util.List;

public interface UserService {
    void addUser(UserDTO userDTO);
    void removeById(Long id);
    void modifyUser(UserDTO example);
    UserVO getById(Long id);
    List<UserVO> getAll();

    PageResult<UserVO> getByPage(long pageCurrent, long pageSize);

    List<UserVO> getRequired(Integer minAge, Integer maxAge);

    List<UserVO> getPart();

    UserVO getByName(String name);

    void register(UserDTO userDTO);

    String login(LoginDTO loginDTO);


}

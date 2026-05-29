package io.github.singlestructuretemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.singlestructuretemplate.enums.ResultCodeEnum;
import io.github.singlestructuretemplate.exception.BusinessException;
import io.github.singlestructuretemplate.mapper.UserMapper;
import io.github.singlestructuretemplate.pojo.LoginDTO;
import io.github.singlestructuretemplate.pojo.UserDTO;
import io.github.singlestructuretemplate.pojo.UserEntity;
import io.github.singlestructuretemplate.pojo.PageResult;
import io.github.singlestructuretemplate.service.UserService;
import io.github.singlestructuretemplate.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${jwt.expire-hours}")
    private long tokenExpireTime;

    @Override
    public void addUser(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);
        userMapper.insert(userEntity);
    }

    @Override
    public void removeById(Long id) {
        int rows = userMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(),"用户不存在,操作失败");
        }
    }

    @Override
    public void modifyUser(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        int rows = userMapper.updateById(userEntity);
        if (rows == 0) {
            log.warn("更新用户受影响行数为0，ID: {}, DTO: {}", userDTO.getId(), userDTO);
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "更新失败或用户不存在");
        }
    } // 没传的字段不改

    @Override
    public UserEntity getById(Long id) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(),"用户不存在,操作失败");
        }
        return user;
    }

    @Override
    public List<UserEntity> getAll() {
        return userMapper.selectList(null);
    }

    @Override
    public PageResult<UserEntity> getByPage(long pageCurrent, long pageSize) {
        IPage<UserEntity> page = new Page<>(pageCurrent,pageSize);
        userMapper.selectPage(page,null);
        return PageResult.of(page);
    }

    @Override
    public List<UserEntity> getRequired(Integer minAge, Integer maxAge) {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.ge(null != minAge, UserEntity::getAge,minAge);
        lqw.le(null != maxAge, UserEntity::getAge,maxAge);
        List<UserEntity> lists = userMapper.selectList(lqw);
        return lists;
    }

    @Override
    public List<UserEntity> getPart() {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserEntity::getName, UserEntity::getAge); //没写的获取为null
        List<UserEntity> lists = userMapper.selectList(lqw);
        return lists;
    }

    @Override
    public UserEntity getByName(String name){
        LambdaQueryWrapper<UserEntity> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(UserEntity::getName, name);
        UserEntity userEntity = userMapper.selectOne(lambdaWrapper);
        return userEntity;
    }

    @Override
    public void register(UserDTO userDTO) {
        UserEntity existUser = getByName(userDTO.getName());
        if (existUser != null) {
            throw new BusinessException(400, "该用户名已被占用");
        }
        String hash = passwordEncoder.encode(userDTO.getPassword());//BCrypt加密
        userDTO.setPassword(hash);
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);
        userMapper.insert(userEntity);
    }


    @Override
    public String login(LoginDTO loginDTO){
        String name = loginDTO.getName();
        String password = loginDTO.getPassword();
        UserEntity userEntity = getByName(name);
        if (userEntity ==null){
            throw new BusinessException(400, "用户不存在");
        }
        if (!passwordEncoder.matches(password, userEntity.getPassword())){ //验证密码
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(),"密码错误");
        }
        Map<String,Object> claims = new HashMap<>();//创建一个 Map 集合，存入登录成功的用户关键信息
        claims.put("id", userEntity.getId());
        claims.put("name", userEntity.getName());
        String token = JwtUtil.genToken(claims);//调用工具类生成加密字符串（Token）

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//从 Redis 模板中获取专门操作字符串（String）类型的对象
        operations.set(token,token,tokenExpireTime, TimeUnit.HOURS);//实际存入,key为token,value为token（占位即可，可以换），设置有效期1小时
        return token;
    }


}

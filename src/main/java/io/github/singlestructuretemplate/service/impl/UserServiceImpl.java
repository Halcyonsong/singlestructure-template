package io.github.singlestructuretemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.singlestructuretemplate.enums.ResultCodeEnum;
import io.github.singlestructuretemplate.exception.BusinessException;
import io.github.singlestructuretemplate.mapper.UserMapper;
import io.github.singlestructuretemplate.pojo.*;
import io.github.singlestructuretemplate.service.UserService;
import io.github.singlestructuretemplate.utils.ConvertUtils;
import io.github.singlestructuretemplate.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final JwtUtil jwtUtil;
    @Value("${jwt.expire-hours}")
    private long tokenExpireTime;

    @Override
    @Transactional
    public void addUser(UserDTO userDTO) {
        if (findEntityByName(userDTO.getName()) != null) {
            throw new BusinessException(400, "该用户名已被占用");
        }
        UserEntity userEntity = ConvertUtils.convert(userDTO, UserEntity.class);
        String hash = passwordEncoder.encode(userDTO.getPassword());
        userEntity.setPassword(hash);
        try {
            userMapper.insert(userEntity);
        } catch (DuplicateKeyException e) {
            // 兜底处理：防止并发导致的数据库唯一键冲突
            throw new BusinessException(400, "该用户名已被占用");
        }
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
        UserEntity userEntity = ConvertUtils.convert(userDTO, UserEntity.class);
        if (userDTO.getPassword()!=null){
            String hash = passwordEncoder.encode(userDTO.getPassword());
            userEntity.setPassword(hash);
        }
        int rows = userMapper.updateById(userEntity);
        if (rows == 0) {
            log.warn("更新用户受影响行数为0，ID: {}, DTO: {}", userDTO.getId(), userDTO);
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "更新失败或用户不存在");
        }
    } // 没传的字段不改

    @Override
    public UserVO getById(Long id) {
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(),"用户不存在,操作失败");
        }
        return ConvertUtils.convert(userEntity, UserVO.class);
    }

    @Override
    public List<UserVO> getAll() {
        List<UserEntity> userEntities = userMapper.selectList(null);
        return ConvertUtils.convertList(userEntities, UserVO.class);
    }

    @Override
    public PageResult<UserVO> getByPage(long pageCurrent, long pageSize) {
        IPage<UserEntity> pageParam = new Page<>(pageCurrent,pageSize);
        userMapper.selectPage(pageParam,null);
        IPage<UserVO> voPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), pageParam.getTotal());
        // 单独转换 records 列表（Entity -> VO）
        voPage.setRecords(ConvertUtils.convertList(pageParam.getRecords(), UserVO.class));
        return PageResult.of(voPage);
    }

    @Override
    public List<UserVO> getRequired(Integer minAge, Integer maxAge) {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.ge(null != minAge, UserEntity::getAge,minAge);
        lqw.le(null != maxAge, UserEntity::getAge,maxAge);
        List<UserEntity> userEntities = userMapper.selectList(lqw);
        return ConvertUtils.convertList(userEntities, UserVO.class);
    }

    @Override
    public List<UserVO> getPart() {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserEntity::getName, UserEntity::getAge); //没写的获取为null
        List<UserEntity> userEntities = userMapper.selectList(lqw);
        return ConvertUtils.convertList(userEntities, UserVO.class);
    }

    @Override
    public UserVO getByName(String name){
        LambdaQueryWrapper<UserEntity> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(UserEntity::getName, name);
        UserEntity userEntity = userMapper.selectOne(lambdaWrapper);
        return ConvertUtils.convert(userEntity, UserVO.class);
    }

    // 内部使用，返回 Entity（含 password）
    private UserEntity findEntityByName(String name) {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserEntity::getName, name);
        return userMapper.selectOne(lqw);
    }


    @Override
    public void register(UserDTO userDTO) {
        UserEntity existUser = findEntityByName(userDTO.getName());
        if (existUser != null) {
            throw new BusinessException(400, "该用户名已被占用");
        }
        UserEntity userEntity = ConvertUtils.convert(userDTO, UserEntity.class);
        String hash = passwordEncoder.encode(userDTO.getPassword()); //BCrypt加密
        userEntity.setPassword(hash);
        try {
            userMapper.insert(userEntity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(400, "该用户名已被占用");
        }
    }

    @Override
    public String login(LoginDTO loginDTO){
        String name = loginDTO.getName();
        String password = loginDTO.getPassword();
        UserEntity userEntity = findEntityByName(name);
        if (userEntity ==null){
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        if (!passwordEncoder.matches(password, userEntity.getPassword())){ //验证密码
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(),"密码错误");
        }
        Map<String,Object> claims = new HashMap<>();//创建一个 Map 集合，存入登录成功的用户关键信息
        claims.put("id", userEntity.getId());
        claims.put("name", userEntity.getName());
        String token = jwtUtil.genToken(claims);//调用工具类生成加密字符串（Token）

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//从 Redis 模板中获取专门操作字符串（String）类型的对象
        operations.set(token,token,tokenExpireTime, TimeUnit.HOURS);//实际存入,key为token,value为token（占位即可，可以换），设置有效期1小时
        return token;
    }


}

package io.github.singlestructuretemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.singlestructuretemplate.mapper.ExampleMapper;
import io.github.singlestructuretemplate.pojo.ExampleDTO;
import io.github.singlestructuretemplate.pojo.ExampleEntity;
import io.github.singlestructuretemplate.pojo.PageResult;
import io.github.singlestructuretemplate.pojo.Result;
import io.github.singlestructuretemplate.service.ExampleService;
import io.github.singlestructuretemplate.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {
    private final ExampleMapper exampleMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${jwt.expire-hours}")
    private long tokenExpireTime;

    @Override
    public void addExample(ExampleDTO exampleDTO) {
        ExampleEntity exampleEntity = new ExampleEntity();
        BeanUtils.copyProperties(exampleDTO,exampleEntity);
        exampleEntity.setCreateTime(LocalDateTime.now());
        exampleEntity.setUpdateTime(LocalDateTime.now());
        exampleMapper.insert(exampleEntity);
    }

    @Override
    public void removeById(Long id) {
        exampleMapper.deleteById(id);
    }

    @Override
    public void modifyExample(ExampleDTO exampleDTO) {
        ExampleEntity exampleEntity = new ExampleEntity();
        BeanUtils.copyProperties(exampleDTO,exampleEntity);
        exampleEntity.setUpdateTime(LocalDateTime.now());
        exampleMapper.updateById(exampleEntity);
    } // 没传的字段不改

    @Override
    public ExampleEntity getById(Long id) {
        return exampleMapper.selectById(id);
    }

    @Override
    public List<ExampleEntity> getAll() {
        return exampleMapper.selectList(null);
    }

    @Override
    public PageResult<ExampleEntity> getByPage(long pageCurrent,long pageSize) {
        IPage<ExampleEntity> page = new Page(pageCurrent,pageSize);
        exampleMapper.selectPage(page,null);
        return PageResult.Setter(page);
    }

    @Override
    public List<ExampleEntity> getRequired(Integer minAge,Integer maxAge) {
        LambdaQueryWrapper<ExampleEntity> lqw = new LambdaQueryWrapper<>();
        lqw.ge(null != minAge,ExampleEntity::getAge,minAge);
        lqw.le(null != maxAge,ExampleEntity::getAge,maxAge);
        List<ExampleEntity> lists = exampleMapper.selectList(lqw);
        return lists;
    }

    @Override
    public List<ExampleEntity> getPart() {
        LambdaQueryWrapper<ExampleEntity> lqw = new LambdaQueryWrapper<>();
        lqw.select(ExampleEntity::getName,ExampleEntity::getAge); //没写的获取为null
        List<ExampleEntity> lists = exampleMapper.selectList(lqw);
        return lists;
    }

    @Override
    public ExampleEntity getByName(String name){
        LambdaQueryWrapper<ExampleEntity> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(ExampleEntity::getName, name);
        ExampleEntity exampleEntity = exampleMapper.selectOne(lambdaWrapper);
        return exampleEntity;
    }

    @Override
    public void register(ExampleDTO exampleDTO) {
        String hash = passwordEncoder.encode(exampleDTO.getPassword());//BCrypt加密
        exampleDTO.setPassword(hash);
        ExampleEntity exampleEntity = new ExampleEntity();
        BeanUtils.copyProperties(exampleDTO,exampleEntity);
        exampleEntity.setCreateTime(LocalDateTime.now());
        exampleEntity.setUpdateTime(LocalDateTime.now());
        exampleMapper.insert(exampleEntity);
    }


    @Override
    public Result login(@RequestBody @Validated ExampleDTO exampleDTO){
        String name = exampleDTO.getName();
        String password = exampleDTO.getPassword();
        ExampleEntity exampleEntity = getByName(name);
        if (exampleEntity==null){
            return Result.error(404,"用户不存在");
        }
        if (!passwordEncoder.matches(password, exampleEntity.getPassword())){ //验证密码
            return Result.error(401,"密码错误");
        }
        Map<String,Object> claims = new HashMap<>();//创建一个 Map 集合，存入登录成功的用户关键信息
        claims.put("id",exampleEntity.getId());
        claims.put("name",exampleEntity.getName());
        String token = JwtUtil.genToken(claims);//调用工具类生成加密字符串（Token）


//    后续可以从token里面获取信息而不用每次去mysql数据库查
//    Map<String, Object> map = ThreadLocalUtil.get();
//    String username = (String) map.get("username");

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//从 Redis 模板中获取专门操作字符串（String）类型的对象
        operations.set(token,token,tokenExpireTime, TimeUnit.HOURS);//实际存入,key为token,value为token（占位即可，可以换），设置有效期1小时
        return Result.success(token);
    }


}

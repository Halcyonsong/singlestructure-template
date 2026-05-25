package io.github.singlestructuretemplate.mapper;

import io.github.singlestructuretemplate.pojo.ExampleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
//只需要写接口，会自动生成实现类并放入IOC容器
public interface ExampleMapper {
    // 1. 增 (Create) 插入一条数据，并获取自增主键
    @Insert("INSERT INTO example_table(name, age) VALUES(#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")    //@Options: useGeneratedKeys=true 告诉 MyBatis 使用数据库自增主键，keyProperty="id" 告诉它把主键值赋给对象的 id 属性
    int insert(ExampleEntity example);

    // 2. 删 (Delete) 根据 ID 删除
    @Delete("DELETE FROM example_table WHERE id = #{id}")
    int deleteById(@Param("id") Long id);    //@Param("id"): 告诉 MyBatis 把方法参数 id 映射到 SQL 语句中的 #{id}，方法参数只有一个时可省略

    // 3. 改 (Update) 根据 ID 更新全部信息
    @Update("UPDATE example_table SET name = #{name}, age = #{age} WHERE id = #{id}")
    int update(ExampleEntity example);   //传入一个实体类对象，MyBatis 会从对象里取出 name、age 和 id，分别填入 SQL 的对应位置

    // 4. 查 (Retrieve)
    // 4.1 根据 ID 查询单条记录
    @Select("SELECT id, name, age FROM example_table WHERE id = #{id}")  //查询全部字段名可用 *
    ExampleEntity selectById(@Param("id") Long id);

    // 4.2 查询所有列表
    @Select("SELECT id, name, age FROM example_table")
    List<ExampleEntity> selectAll();   //多行数据，封装成一个列表返回

    // 4.3 条件查询 (演示多个参数)
    @Select("SELECT id, name, age FROM example_table WHERE name = #{name} AND age > #{age}")
    List<ExampleEntity> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age);  //多个参数时必须 使用 @Param 注解，否则 MyBatis 无法区分






}

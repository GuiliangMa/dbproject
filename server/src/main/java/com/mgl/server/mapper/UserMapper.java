package com.mgl.server.mapper;

import com.mgl.server.controller.dto.UserDTO;
import com.mgl.server.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

//用于实现与数据库的接口
@Mapper
// 注入进容器,在MybatisPlusConfig中配置后，此处注释
public interface UserMapper
{
//  mybaits提供的注解，连接数据库
    public List<User> findAll(String username,
                              String email,
                              String phone);

    @Select("select 1 from sys_user where username=#{username} limit 1")
    public String mapperUsername(String username);

    @Select("select 1 from sys_user where username=#{username} and password=#{password}")
    public String mapperUser(String username,String password);

    @Select("select username,status,nickname from sys_user where username=#{username}")
    public UserDTO getUser(String username);

    @Select("select * from sys_user where username=#{username}")
    public User getUserByUsername(String username);

    @Insert("insert into sys_user(username,password,status,nickname) " +
            "values (#{username},#{password},#{status},#{nickname})")
    public int register(UserDTO userDTO);

    @Insert("INSERT into sys_user(username,password,status,nickname,email,phone) " +
            "VALUES (#{username},#{password},#{status},#{nickname},#{email},#{phone})")
    public int insert(User user);

//    @Update("UPDATE sys_user " +
//            "SET username = #{username}," +
//                "password =#{password}," +
//                "nickname=#{nickname}," +
//                "email=#{email}," +
//                "phone=#{phone}," +
//                "address=#{address}  " +
//            "WHERE id = #{id}")
    public int update(User user);

    @Delete("delete from sys_user where username = #{username}")
    public Integer deleteByUserName(@Param("username") String username);
    //    @Param 中参数要与@Delete中未知参数一致

    //    @Select("SELECT * FROM sys_user LIMIT #{pageNum},#{pageSize}")
    //    手写分页
    public List<User> searchPage(Integer pageNum,
                                 Integer pageSize,
                                 String username,
                                 String email,
                                 String phone);


//    @Select("SELECT COUNT(*) FROM sys_user where username like concat('%',#{username},'%') and ")
    int countTotal(String username,String email,String phone);
}

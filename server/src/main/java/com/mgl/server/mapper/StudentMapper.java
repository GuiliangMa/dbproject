package com.mgl.server.mapper;

import com.mgl.server.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper
{
    List<Student> findPage(Integer pageNum,
                           Integer pageSize,
                           String sid,
                           String name,
                           String username);

    int countTotal(Integer pageNum,
                   Integer pageSize,
                   String sid,
                   String name,
                   String username);

    @Select("select 1 from student where sid =#{sid}")
    public String mapperSid(String sid);

    @Insert("insert into student(sid,name,gender,username)" +
            "values(#{sid},#{name},#{gender},#{username})")
    public int save(Student student);

    public int update(Student student);
}

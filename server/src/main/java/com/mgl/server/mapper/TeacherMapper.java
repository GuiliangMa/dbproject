package com.mgl.server.mapper;

import com.mgl.server.entity.Teacher;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherMapper
{
    List<Teacher> findPage(Integer pageNum,
                           Integer pageSize,
                           String tid,
                           String name,
                           String username);

    int countTotal(String tid,
                   String name,
                   String username);

    @Select("select 1 from teacher where tid =#{tid}")
    public String mapperTid(String tid);

    @Insert("insert into teacher(tid,name,gender,username)" +
            "values(#{tid},#{name},#{gender},#{username})")
    public int save(Teacher teacher);

    void update(Teacher teacher);
}

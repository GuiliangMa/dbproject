package com.mgl.server.mapper;

import com.mgl.server.common.Result;
import com.mgl.server.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper
{
    List<Course> findPage(Integer pageNum, Integer pageSize, String cid, String cname, Double credit, String ctype);

    int countTotal(Integer pageNum, Integer pageSize, String cid, String cname, Double credit, String ctype);

    @Select("select 1 from course where cid = #{cid}")
    String checkRepeat(String cid);

    @Insert("insert into course(cid,cname,credit,ctype) values(#{cid},#{cname},#{credit},#{ctype})")
    public int save(Course course);

    public int update(Course course);

    @Delete("delete from course where cid = #{cid}")
    public int delete(String cid);

    @Select("select * from course")
    public List<Course> list();

    @Select("select tcid from teachercourse where cid=#{cid} and state='已同意'")
    int getTicd(String cid);
}

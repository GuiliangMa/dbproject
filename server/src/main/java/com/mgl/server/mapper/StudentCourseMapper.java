package com.mgl.server.mapper;

import cn.hutool.core.date.DateTime;
import com.mgl.server.entity.StudentCourse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentCourseMapper
{

    List<StudentCourse> findPage(Integer pageNum, Integer pageSize, String cid,String roomName,
                                 String name, String cname, String sid);

    int countTotal(String cid, String roomName ,String name, String cname, String sid);

    @Delete("delete from sc where sid=#{sid} and cid=#{cid}")
    int deleteBySidAndTcid(String sid, String cid);

    @Select("select sid from student where username=#{username}")
    String getSid(String username);
}

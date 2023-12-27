package com.mgl.server.mapper;

import com.mgl.server.entity.StudentCourse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeachMapper
{
    List<StudentCourse> findPage(Integer pageNum, Integer pageSize, String cid, String roomName,String name, String cname,String tid);

    int countTotal(String cid, String roomName, String name, String cname,String tid);
    @Delete("delete from tc where tid=#{sid} and cid=#{cid}")
    int deleteByTidAndTcid(String tid, String cid);

    @Select("select 1 from course where cid=#{cid}")
    String checkCid(String cid);

    @Select("select 1 from teacher where tid=#{tid}")
    String checkTid(String tid);

    @Select("select 1 from tc where cid=#{cid}")
    String checkRepeat(String cid);

    @Insert("insert into tc(tid,cid) values(#{tid},#{cid})")
    int handleAddTeacher(String tid, String cid);

    @Select("select tid from teacher where username=#{username}")
    String getTid(String username);
}

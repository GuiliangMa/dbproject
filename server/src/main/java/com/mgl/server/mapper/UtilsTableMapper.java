package com.mgl.server.mapper;

import cn.hutool.core.date.DateTime;
import com.mgl.server.controller.dto.UtilsTableDTO;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.UtilsTable;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface UtilsTableMapper
{
    public List<UtilsTableDTO> findPage(int pageNum, int pageSize,
                                        Date startTime,Date endTime,
                                        String roomName,String name,String status,
                                        String state,Date now,String props);

    @Select("select name from student where username = #{username}")
    public String getStudentNameByUsername(String username);

    @Select("select name from teacher where username = #{username}")
    public String getTeacherNameByUsername(String username);

    public int countTotal(Date startTime,Date endTime,
                          String roomName,String name,String status,
                          String state,Date now,String props);

    @Delete("delete from utilization where uid = #{uid}")
    public int deleteById(Integer uid);

    List<UtilsTableDTO> list();

    @Insert("insert into utilization(username, rid, starttime, endtime,remark,props)" +
            "values(#{username},#{rid},#{startTime},#{endTime},#{remark},#{props})")
    public int insert(String username,int rid,Date startTime,Date endTime,String remark,String props);
    
    List<UtilsTableDTO> findMyPage(Integer pageNum, Integer pageSize, Date startTime, Date endTime, String roomName, String username, String state, DateTime now);

    int countMyTotal(Date startTime, Date endTime, String roomName, String username, String state, DateTime now);

    @Select("select asgid from utilization where uid=#{uid}")
    int getAsgid(int uid);
}

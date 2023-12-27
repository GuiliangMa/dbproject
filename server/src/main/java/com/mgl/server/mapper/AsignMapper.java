package com.mgl.server.mapper;

import cn.hutool.core.date.DateTime;
import com.mgl.server.controller.dto.AsignDTO;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.controller.dto.UtilsTableDTO;
import com.mgl.server.entity.Asign;
import com.mgl.server.entity.Classroom;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface AsignMapper
{
    @Insert("insert into asign(username, remark, capacity, multiMedia, ac, type, starttime, endtime, state)" +
            " VALUES(#{username},#{remark},#{capacity},#{multiMedia},#{ac},#{type},#{startTime},#{endTime},'未分配')")
    int assign(Asign as);

    @Select("select * from asign where asgid=#{asgid}")
    AsignDTO getEntity(int asgid);

    @Select("select asgid from asign " +
            "where username=#{username} and remark=#{remark} and capacity=#{capacity} and multiMedia=#{multiMedia} and ac=#{ac} and type=#{type} and starttime=#{startTime} and endtime=#{endTime}")
    String getAsgid(AsignDTO ass);

    List<ClassroomDTO> findList(DateTime startTime, DateTime endTime);

    @Insert("insert into utilization(rid, username, starttime, endtime, remark, props, asgid) " +
            "values(#{newid},#{username},#{startTime},#{endTime},#{remark},#{props},#{asgid})")
    void reassign(int newid, String username, DateTime startTime, DateTime endTime, String remark, String props, int asgid);

    @Insert("insert into message(fromuser, touser, msg, sendtime) VALUES(#{admin},#{username},#{s},#{date})")
    void sendMessage(String admin, String username, String s, DateTime date);

    @Update("update asign set state='已分配' where asgid=#{asgid}")
    void success(int asgid);

    @Select("select asgid from asign where state='未分配'")
    List<Integer> List();

    @Select("select asgid from asign where #{now}>starttime and state='未分配'")
    List<Integer> passList(Date now);

    @Update("update asign set state='已过期' where asgid=#{asgid}")
    void updatepass(int asgid);

    @Select("select username from asign where asgid=#{asgid}")
    String getUsername(int asgid);

    @Update("update asign set state='未分配' where asgid=#{asgid}")
    void drop(int asgid);

    List<AsignDTO> findPage(Integer pageNum, Integer pageSize, Date startTime, Date endTime, String roomName, String username, String state);

    int countTotal(Date startTime, Date endTime, String roomName, String username, String state);

    @Delete("delete from utilization where asgid=#{asgid}")
    void deleteUidByAsgid(Integer asgid);

    @Delete("delete from asign where asgid=#{asgid}")
    void deleteAsgid(Integer asgid);
}

package com.mgl.server.mapper;

import cn.hutool.core.date.DateTime;
import com.mgl.server.controller.dto.ApplyDTO;
import com.mgl.server.entity.Apply;
import com.mgl.server.entity.Classroom;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface ApplyMapper
{
    List<Classroom> findFreePage(Integer pageNum, Integer pageSize,
                                 Date startTime, Date endTime,String roomName,
                                 Integer capacity, Integer multiMedia,
                                 Integer AC, String type);

    public int totalFree(Date startTime,Date endTime,String roomName,
                         Integer capacity, Integer multiMedia,
                         Integer AC, String type);

    public Map<String,Object> getIdAndName(String username);

    @Insert("insert into apply(username,rid,starttime,endtime,remark,mode)" +
            "value(#{username},#{rid},#{startTime},#{endTime},#{remark},'待批准')")
    public int apply(ApplyDTO applyDTO);

    @Select("select * from apply " +
            "where username=#{username} and rid=#{rid} and starttime=#{startTime} and endtime=#{endTime} and mode <> '被拒绝'")
    public String checkRepeat(ApplyDTO applyDTO);

    @Select("select 1 from utilization where rid=#{rid} and endTime > #{startTime} and #{endTime} > startTime")
    String checkAvailabel(ApplyDTO applyDTO);

    List<ApplyDTO> findPage(Integer pageNum, Integer pageSize, Date startTime, Date endTime, String roomName, String name, String status, String mode);

    int countTotal(Date startTime, Date endTime, String roomName, String name, String status, String mode);

    @Select("select mode from apply where apid=#{apid}")
    public String findMode(int apid);

    @Update("update apply set mode = '已同意' where apid=#{apid}")
    public int accept(int apid);

    public int modify(int rid,Date startTime,Date endTime);

    List<ApplyDTO> findMyPage(Integer pageNum, Integer pageSize, Date startTime, Date endTime, String roomName, String username, String mode);

    int countMyTotal(Date startTime, Date endTime, String roomName, String username, String mode);

    @Update("update apply set msg = #{msg},mode='已拒绝' where apid=#{apid}")
    int refuse(Apply apply);

    @Select("select mode from apply where apid=#{apid}")
    String findState(Integer apid);

    @Update("update apply set mode='已撤回' where apid=#{apid}")
    void revoke(Integer apid);

    @Insert("insert into message(fromuser, touser, msg,sendtime) values (#{admin},#{username},#{s},#{now})")
    void sendMessage(String admin, String username, String s,Date now);

    @Select("select apid,username,roomname  from apply natural join classroom where mode = '待批准' and endTime > #{startTime} and #{endTime} > startTime and rid = #{rid}")
    List<ApplyDTO> modifyList(int rid, Date startTime, Date endTime);

    @Update("update apply set msg = #{msg},mode='已拒绝' where apid=#{apid}")
    void refuseByApid(int apid, String msg);
}

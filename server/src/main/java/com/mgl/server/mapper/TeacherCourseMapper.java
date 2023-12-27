package com.mgl.server.mapper;

import cn.hutool.core.date.DateTime;
import com.mgl.server.controller.dto.ApplyDTO;
import com.mgl.server.entity.Apply;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.TeacherCourse;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface TeacherCourseMapper
{
    public List<TeacherCourse> findPage(Integer pageNum, Integer pageSize,String cid,
                                        String roomName, String cname,
                                        String mode, Date now);

    public int countTotal(String cid,String roomName, String cname,
                          String mode, Date now);

    @Delete("delete from teachercourse where tcid=#{tcid}")
    int deleteByTcid(int tcid);

    List<TeacherCourse> list();

    @Select("select * from classroom")
    List<Classroom> classroomList();

    @Select("select 1 from utilization where rid=#{rid} and endtime>#{classStart} and" +
            " #{classEnd}>starttime and props='课用'")
    List<Integer> check(int rid, Date classStart, Date classEnd);

    @Select("select tid from teacher where username=#{username}")
    String getTid(String username);

    @Insert("insert into teachercourse(username, cid, rid, startDate, endDate, weekday, beginTime, endTime, times, state) " +
            "VALUES(#{username},#{cid},#{rid},#{startDate},#{endDate},#{weekday},#{beginTime},#{endTime},#{times},'待批准')")
    int application(TeacherCourse teacherCourse);

    @Select("select 1 from teacher where tid=#{tid}")
    public String checkTid(String tid);

    @Select("select 1 from course where cid=#{cid}")
    public String checkCid(String cid);

    List<TeacherCourse> findTrial(Integer pageNum, Integer pageSize, String cid, String roomName, String username, String cname, String state);

    int countTotalTrial(String cid, String roomName, String username, String cname, String state);

    @Select("select * from teachercourse where tcid=#{tcid}")
    TeacherCourse getEntity(Integer tcid);

    @Update("update teachercourse set state='已同意' where tcid = #{tcid}")
    void accept(Integer tcid);

    @Update("update apply set mode = '已拒绝',msg='该时段与课程冲突' " +
            "where rid = #{rid} and endTime > #{startTime} and #{endTime} > startTime and mode='待批准'")
    void conflict(int rid, Date startTime, Date endTime);

    
    @Select("select username from teachercourse where tcid=#{tcid}")
    String getUsername(int tcid);

    @Insert("insert into utilization(rid, username, starttime, endtime, remark, props) " +
            "VALUES(#{rid},#{username},#{startTime},#{endTime},#{remark},#{props}) ")
    void accpetInsert(int rid, String username, Date startTime, Date endTime, String remark, String props);

    @Select("select * from  apply where rid = #{rid} and endTime > #{startTime} and #{endTime} > startTime and mode='已同意'")
    List<ApplyDTO> agreelist(int rid, Date startTime, Date endTime);
    @Update("update apply set mode = '已拒绝',msg='您所申请的教室与课程冲突，并且暂时没有合适的教室可以分配给您，请重新申请' " +
        "where apid=#{apid}")
    void conflictAccepte(int apid);

    @Select("select uid from utilization where rid = #{rid} and endTime > #{startTime} and #{endTime} > startTime")
    List<Integer> conflictAccepteTable(int rid, Date startTime, Date endTime);

    @Select("select * from teachercourse " +
            "where rid=#{rid} and endDate > #{startDate} and #{endDate}> startDate and weekday = #{weekday} and #{endTime} >beginTime and endTime>#{beginTime} and tcid<>#{tcid}")
    List<TeacherCourse> conflictList(TeacherCourse tc);

    @Update("update teachercourse set state='已拒绝', remark=#{s} where tcid = #{tcid}")
    void refuse(int tcid, String s);

    List<Classroom> findAllocate(Apply apply);

    @Select("select * from classroom where rid=#{rid}")
    Classroom findClassroom(int rid);

    @Update("update apply set msg=#{s} where apid=#{apid}")
    void conflictChange(int apid, String s);

    @Insert("insert into utilization(rid, username, starttime, endtime, remark, props) " +
            "VALUES(#{newid},#{username},#{startTime},#{endTime},#{remark},#{props})")
    void usage(int newid, String username, Date startTime, Date endTime, String remark, String props);

    List<TeacherCourse> findPersonalPage(Integer pageNum, Integer pageSize, String cid, String roomName, String tid, String cname, String mode, DateTime now);

    int countPersonalTotal(String cid, String roomName, String tid, String cname, String mode, DateTime now);

    List<TeacherCourse> findApproval(Integer pageNum, Integer pageSize, String cid, String roomName, String tid, String cname, String state);

    int countApproval(String cid, String roomName, String tid, String cname, String state);

    @Select("select name from student where sid=#{sid}")
    String findStudentName(String sid);

    @Insert("insert into sc(sid,cid) values(#{sid},#{cid})")
    int handleAddStudent(String sid, String cid);

    @Select("select 1 from sc where sid=#{sid} and cid=#{cid}")
    String checkRepeat(String sid, String cid);

    @Select("select 1 from teachercourse where tcid=#{tcid}")
    String checkTcid(int tcid);

    @Select("select uid from utilization where rid=#{rid} and username=#{username} and starttime=#{classStart} and endtime=#{classEnd} and remark=#{remark} and props=#{props}")
    int getUid(int rid, String username, Date classStart, Date classEnd, String remark, String props);

    @Delete("delete from utilization where uid=#{uid}")
    void deleteByUid(int uid);

    @Select("select 1 from sys_user where username=#{username}")
    String checkUsername(String username);

    @Select("select 1 from teachercourse where cid=#{cid} and state='已同意'")
    String checkCidAvailable(String cid);

    @Select("select 1 from tc where cid=#{cid}")
    String checkAble(String cid);

    @Select("select status from sys_user where username=#{username}")
    String getStatus(String username);

    @Select("select 1 from tc where tid=#{tid} and cid=#{cid}")
    String checkCourse(String tid, String cid);

    @Insert("insert into message(fromuser, touser, msg,sendtime) values (#{admin},#{username},#{s},#{now})")
    void sendMessage(String admin, String username, String s,Date now);

    @Select("select asgid from utilization where uid=#{uid}")
    int getAsgid(int uid);

//    @Insert("insert into utilization(starttime) value(#{start})")
//    void insert(Date start);
}


package com.mgl.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.*;
import com.mgl.server.entity.Apply;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.TeacherCourse;
import com.mgl.server.mapper.ApplyMapper;
import com.mgl.server.mapper.AsignMapper;
import com.mgl.server.mapper.TeacherCourseMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TeacherCourseService
{
    @Autowired
    TeacherCourseMapper teacherCourseMapper;
    @Autowired
    ApplyMapper applyMapper;
    @Autowired
    AsignService asignService;
    @Autowired
    AsignMapper asignMapper;

    public String Trans(int number)
    {
        if (number == 1) return "周日";
        else if (number == 2) return "周一";
        else if (number == 3) return "周二";
        else if (number == 4) return "周三";
        else if (number == 5) return "周四";
        else if (number == 6) return "周五";
        else if (number == 7) return "周六";
        else return null;
    }

    public Result findPage(Integer pageNum, Integer pageSize, String cid,
                           String roomName, String cname, String mode)
    {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<TeacherCourse> data = teacherCourseMapper.findPage(pageNum, pageSize, cid,
                roomName, cname, mode, now);
        for (TeacherCourse msg : data)
        {
            if (DateUtil.compare(now, msg.getStartDate()) < 0) msg.setMode("待使用");
            if (DateUtil.compare(now, msg.getStartDate()) >= 0 && DateUtil.compare(now, msg.getEndDate()) <= 0)
                msg.setMode("在使用");
            if (DateUtil.compare(now, msg.getEndDate()) > 0) msg.setMode("已使用");
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = teacherCourseMapper.countTotal(cid, roomName, cname, mode, now);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = false,isolation = Isolation.SERIALIZABLE)
    public Result delete(int tcid)
    {

        TeacherCourse tc = teacherCourseMapper.getEntity(tcid);
        System.out.println("~~~~~~~~");
        System.out.println(tcid);
        System.out.println(tc);
        System.out.println("~~~~~~~~");
        int rid = tc.getRid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(tc.getStartDate());
        String endDate = sdf.format(tc.getEndDate());
        int weekday = tc.getWeekday();
        String beginTime = tc.getBeginTime();
        String endTime = tc.getEndTime();
        int times = tc.getTimes();
        Date dayStart = DateUtil.parse(startDate + " " + beginTime);
        Date dayEnd = DateUtil.parse(startDate + " " + endTime);
        int nowWeekday = DateUtil.dayOfWeek(dayStart) - 1;
        int offset = 0;
        int Weekday = weekday - 1;
        if (Weekday >= nowWeekday)
            offset = Weekday - nowWeekday;
        else
            offset = Weekday + 7 - nowWeekday;
        dayStart = DateUtil.offsetDay(dayStart, offset);
        dayEnd = DateUtil.offsetDay(dayEnd, offset);
        Date classStart = dayStart;
        Date classEnd = dayEnd;
        Date LastDay = DateUtil.parse(endDate);
        while (DateUtil.compare(classEnd, LastDay) < 0)
        {
            // 3）在临时使用表中加入该课程使用
            String username = teacherCourseMapper.getUsername(tc.getTcid());
            int uid = teacherCourseMapper.getUid(rid,username,classStart,classEnd,"课程使用","课用");
            teacherCourseMapper.deleteByUid(uid);


            for (int i = 1; i <= times; i++)
            {
                classStart = DateUtil.offsetDay(classStart, 7);
                classEnd = DateUtil.offsetDay(classEnd, 7);
            }
        }
        int deleteRes = teacherCourseMapper.deleteByTcid(tcid);
        asignService.tryBatchReassign(0);
        if (deleteRes != 0) return Result.success(deleteRes);
        else return Result.error("500", "删除失败");
    }


    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<TeacherCourse> list()
    {
        return teacherCourseMapper.list();
    }

    public String dateFormat(String dateTime)
    {
        dateTime = dateTime.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date time = format.parse(dateTime);
            String result = defaultFormat.format(time);
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String timeFormat(String dateTime)
    {
        dateTime = dateTime.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("HH:mm:ss");
        try
        {
            Date time = format.parse(dateTime);
            String result = defaultFormat.format(time);
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    public Result findRoom(Integer pageNum, Integer pageSize,
                           String startDate, String endDate, String weekday,
                           String beginTime, String endTime, Integer times)
    {
        if (StrUtil.isBlank(startDate) || StrUtil.isBlank(endDate) || StrUtil.isBlank(weekday)
                || StrUtil.isBlank(beginTime) || StrUtil.isBlank(endTime))
            return Result.error("500", "参数不全");
        pageNum = (pageNum - 1) * pageSize;


        Date dayStart = DateUtil.parse(dateFormat(startDate) + " " + beginTime);
        Date dayEnd = DateUtil.parse(dateFormat(startDate) + " " + endTime);
        int nowWeekday = DateUtil.dayOfWeek(dayStart) - 1;
        int offset = 0;
        if (StrUtil.isBlank(weekday))
            offset = 0;
        else
        {
            int Weekday = Integer.parseInt(weekday) - 1;
            if (Weekday >= nowWeekday)
                offset = Weekday - nowWeekday;
            else
                offset = Weekday + 7 - nowWeekday;
        }
        dayStart = DateUtil.offsetDay(dayStart, offset);
        dayEnd = DateUtil.offsetDay(dayEnd, offset);

        List<Classroom> tempRoomList = teacherCourseMapper.classroomList();
        List<Classroom> Rooms = new ArrayList<>();
        for (Classroom classroom : tempRoomList)
        {
            int rid = classroom.getRid();
            Date classStart = dayStart;
            Date classEnd = dayEnd;
            Date LastDay = DateUtil.parse(endDate);
            boolean check = true;
            while (DateUtil.compare(classEnd, LastDay) < 0)
            {
                List<Integer> temp = teacherCourseMapper.check(rid, classStart, classEnd);
                if (temp.size() != 0)
                {
                    check = false;
                    break;
                }
                for (int i = 1; i <= times; i++)
                {
                    classStart = DateUtil.offsetDay(classStart, 7);
                    classEnd = DateUtil.offsetDay(classEnd, 7);
                }
            }
            if (check)
                Rooms.add(classroom);
        }
        if (Rooms.size() == 0)
            return Result.error("500", "无可用教室");

        List<ClassroomDTO> data = new ArrayList<>();

        for (int i = 0; i < Math.min(pageSize, Rooms.size()); i++)
        {
            data.add(new ClassroomDTO(Rooms.get(i)));
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", data);
        res.put("total", Rooms.size());
        System.out.println("~~~~~~~~~");
        return Result.success(res);
    }

    @Transactional(readOnly = true)
    public Result getTid(String username)
    {
        String ans = teacherCourseMapper.getTid(username);
        Map<String, Object> data = new HashMap<>();
        data.put("tid", ans);
        return Result.success(data);
    }


    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public Result apply(@NotNull TeacherCourseDTO teacherCourseDTO)
    {
        // 〇检查教工号和课程号是否合法
        String username = teacherCourseDTO.getUsername();
        String cid = teacherCourseDTO.getCid();

        String status=teacherCourseMapper.getStatus(username);
        if(Objects.equals(status, "教师"))
        {
            String tid=teacherCourseMapper.getTid(username);
            String checkCourse=teacherCourseMapper.checkCourse(tid,cid);
            if(StrUtil.isBlank(checkCourse)){
                return Result.error("500","您并不教授该课程");
            }
        }

        if (StrUtil.isBlank(teacherCourseMapper.checkUsername(username)) || StrUtil.isBlank(teacherCourseMapper.checkCid(cid)))
        {
            return Result.error("500", "课程号不存在");
        }

        System.out.println("~~~~~~~");

        System.out.println(teacherCourseMapper.checkCidAvailable(cid));

        System.out.println("~~~~~~~");
        if(StrUtil.isNotBlank(teacherCourseMapper.checkCidAvailable(cid))){
            return Result.error("500","该课程已分配了教室,请检查课序号");
        }

        // ①检查教室是否仍为可使用
        int rid = teacherCourseDTO.getRid();
        String startDate = teacherCourseDTO.getStartDate();
        String endDate = teacherCourseDTO.getEndDate();
        int weekday = teacherCourseDTO.getWeekday();
        String beginTime = teacherCourseDTO.getBeginTime();
        String endTime = teacherCourseDTO.getEndTime();

        int times = teacherCourseDTO.getTimes();
        Date dayStart = DateUtil.parse(dateFormat(startDate) + " " + beginTime);
        Date dayEnd = DateUtil.parse(dateFormat(startDate) + " " + endTime);
        int nowWeekday = DateUtil.dayOfWeek(dayStart) - 1;
        int offset = 0;
        int Weekday = weekday - 1;
        if (Weekday >= nowWeekday)
            offset = Weekday - nowWeekday;
        else
            offset = Weekday + 7 - nowWeekday;
        dayStart = DateUtil.offsetDay(dayStart, offset);
        dayEnd = DateUtil.offsetDay(dayEnd, offset);
        Date classStart = dayStart;
        Date classEnd = dayEnd;
        Date LastDay = DateUtil.parse(endDate);
        boolean check = true;
        while (DateUtil.compare(classEnd, LastDay) < 0)
        {
            List<Integer> temp = teacherCourseMapper.check(rid, classStart, classEnd);
            if (temp.size() != 0)
            {
                check = false;
                break;
            }
            for (int i = 1; i <= times; i++)
            {
                classStart = DateUtil.offsetDay(classStart, 7);
                classEnd = DateUtil.offsetDay(classEnd, 7);
            }
        }
        if (! check)
            return Result.error("500", "该时段下与其他课程冲突");

        // ②加入待申请列表
//        teacherCourseDTO.setBeginTime(timeFormat(beginTime));
//        teacherCourseDTO.setEndTime(timeFormat(endTime));
        int res = teacherCourseMapper.application(new TeacherCourse(teacherCourseDTO));

        return Result.success(1);
    }

    @Transactional(readOnly = true)
    public Result findTrialMessage(Integer pageNum, Integer pageSize, String roomName, String username, String cid, String cname, String state)
    {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<TeacherCourse> data = teacherCourseMapper.findTrial(pageNum, pageSize, cid,
                roomName, username, cname, state);
        for (TeacherCourse msg : data)
        {
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = teacherCourseMapper.countTotalTrial(cid, roomName, username, cname, state);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public Result accept(iDTO dto)
    {
        int tcid=dto.getTcid();
        String admin= dto.getUsername();

        System.out.println("~~~~~~~~~~~~~");
        System.out.println(tcid);
        System.out.println(admin);
        System.out.println("~~~~~~~~~~~~~");

        TeacherCourse tc = teacherCourseMapper.getEntity(tcid);
        // ①检查该信息是否已经被审批
        if (! Objects.equals(tc.getState(), "待批准"))
            return Result.error("500", "该信息已被处理");
        // ②修改审批状态为已同意
        teacherCourseMapper.accept(tcid);
        teacherCourseMapper.sendMessage(admin,tc.getUsername(),"您申请的课程号为："+tc.getCid()+" 的教室已申请成功",DateUtil.date());

        // ③对该信息的每一次使用教室日期时间，临时 待批准的拒绝，临时 已同意的拒绝（与课程冲突）,增加当天使用

        // // 1）把临时使用中与该课程使用冲突的 待批准的设置为已拒绝，remark=与课程安排冲突
        // // 2）把临时使用中与该课程使用冲突的 已同意的设置为已拒绝，remark=与课程安排冲突
        // // 3）在临时使用表中加入该课程使用

        int rid = tc.getRid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(tc.getStartDate());
        String endDate = sdf.format(tc.getEndDate());
        int weekday = tc.getWeekday();
        String beginTime = tc.getBeginTime();
        String endTime = tc.getEndTime();
        int times = tc.getTimes();
        Date dayStart = DateUtil.parse(startDate + " " + beginTime);
        Date dayEnd = DateUtil.parse(startDate + " " + endTime);
        int nowWeekday = DateUtil.dayOfWeek(dayStart) - 1;
        int offset = 0;
        int Weekday = weekday - 1;
        if (Weekday >= nowWeekday)
            offset = Weekday - nowWeekday;
        else
            offset = Weekday + 7 - nowWeekday;
        dayStart = DateUtil.offsetDay(dayStart, offset);
        dayEnd = DateUtil.offsetDay(dayEnd, offset);
        Date classStart = dayStart;
        Date classEnd = dayEnd;
        Date LastDay = DateUtil.parse(endDate);
        while (DateUtil.compare(classEnd, LastDay) < 0)
        {
            // 1）把临时使用中与该课程使用冲突的 待批准的设置为已拒绝，remark=与课程安排冲突
            List<ApplyDTO> temp =applyMapper.modifyList(rid,classStart,classEnd);
            for(ApplyDTO ap:temp)
            {
                applyMapper.refuseByApid(ap.getApid(),"该时段此教室被其他人占用");
                applyMapper.sendMessage(admin,ap.getUsername(),"您申请的"+ap.getRoomName()+"教室，已拒绝 "+" 该时段与课程冲突",DateUtil.date());
            }
            // 2）把临时使用中与该课程使用冲突的 已同意的设置为重新调度已拒绝，remark=与课程安排冲突

            List<Integer> conflictList=teacherCourseMapper.conflictAccepteTable(rid,classStart,classEnd);
            for(int uid:conflictList){
                int asgid=teacherCourseMapper.getAsgid(uid);
                if(uid!=0){
                    asignMapper.drop(asgid);
                    asignMapper.sendMessage("admin",asignMapper.getUsername(asgid),"您请求教室信息 "+asgid+" 获得的教室，现已经被收回",DateUtil.date());
                }
                teacherCourseMapper.deleteByUid(uid);
            }


            List<ApplyDTO> agreeList = teacherCourseMapper.agreelist(rid,classStart,classEnd);
            for(ApplyDTO apply:agreeList){
                System.out.println("~~~~~~");
                System.out.println(apply);
                Apply allocate = new Apply(apply);

                int newid=0;
                String newName=null;

                Classroom old = teacherCourseMapper.findClassroom(allocate.getRid());
                List<Classroom> allocateClassroom = teacherCourseMapper.findAllocate(allocate);
                for(Classroom room:allocateClassroom){
                    if(Classroom.isSimlar(old,room)){
                        newid=room.getRid();
                        newName=room.getRoomName();
                        break;
                    }
                }
                System.out.println("~~~~~~~~");
                System.out.println(newid);
                System.out.println("~~~~~~~~");
                if(newid!=0){
                    teacherCourseMapper.conflictChange(apply.getApid(),"您之前申请的教室先被课程占用，先调换为"+newName);
                    teacherCourseMapper.sendMessage(admin,apply.getUsername(),"您之前申请的教室"+old.getRoomName()+"先被课程占用，先调换为"+newName,DateUtil.date());
                    teacherCourseMapper.usage(newid,apply.getUsername(),apply.getStartTime(),apply.getEndTime(),apply.getRemark()+"(被调换)","临时");
                }
                else{
                    teacherCourseMapper.conflictAccepte(apply.getApid());
                    teacherCourseMapper.sendMessage(admin,apply.getUsername(),"您之前申请的教室"+old.getRoomName()+"与课程使用冲突，并且暂时没有合适的教室可以分配给您，请重新申请",DateUtil.date());
                }
            }



            // 3）在临时使用表中加入该课程使用
            String username = teacherCourseMapper.getUsername(tc.getTcid());
            System.out.println(username);
            teacherCourseMapper.accpetInsert(rid,username,classStart,classEnd,"课程使用","课用");


            for (int i = 1; i <= times; i++)
            {
                classStart = DateUtil.offsetDay(classStart, 7);
                classEnd = DateUtil.offsetDay(classEnd, 7);
            }
        }

        // ④对于时段冲突且时间段冲突且工作日冲突 的检查可用性，若不再可用设为拒绝

        List<TeacherCourse> conflictList = teacherCourseMapper.conflictList(tc);
        System.out.println(conflictList);
        for (TeacherCourse thc : conflictList)
        {
            rid = thc.getRid();
            startDate = sdf.format(thc.getStartDate());
            endDate = sdf.format(thc.getEndDate());

            weekday = thc.getWeekday();
            beginTime = thc.getBeginTime();
            endTime = thc.getEndTime();
            times = thc.getTimes();
            dayStart = DateUtil.parse(startDate + " " + beginTime);
            dayEnd = DateUtil.parse(startDate + " " + endTime);
            nowWeekday = DateUtil.dayOfWeek(dayStart) - 1;
            offset = 0;
            Weekday = weekday - 1;
            if (Weekday >= nowWeekday)
                offset = Weekday - nowWeekday;
            else
                offset = Weekday + 7 - nowWeekday;
            dayStart = DateUtil.offsetDay(dayStart, offset);
            dayEnd = DateUtil.offsetDay(dayEnd, offset);
            classStart = dayStart;
            classEnd = dayEnd;
            LastDay = DateUtil.parse(endDate);
            boolean check = true;
            while (DateUtil.compare(classEnd, LastDay) < 0)
            {
                List<Integer> temp = teacherCourseMapper.check(rid, classStart, classEnd);
                if (temp.size() != 0)
                {
                    check = false;
                    break;
                }
                for (int i = 1; i <= times; i++)
                {
                    classStart = DateUtil.offsetDay(classStart, 7);
                    classEnd = DateUtil.offsetDay(classEnd, 7);
                }
            }
            if(!check){
                teacherCourseMapper.refuse(thc.getTcid(),"与其他课程时间冲突,请再次申请");
                teacherCourseMapper.sendMessage(admin,thc.getUsername(),"您为申请的课程号为："+thc.getCid()+" 的教室与其他课程时间冲突,请再次申请",DateUtil.date());
            }
        }
        asignService.tryBatchReassign(0);
        return Result.success("success");
    }

    @Transactional(readOnly = true,isolation = Isolation.REPEATABLE_READ)
    public Result findPersonalPage(Integer pageNum, Integer pageSize, String roomName, String username, String cid, String cname, String mode)
    {
        pageNum = (pageNum - 1) * pageSize;
        String tid=teacherCourseMapper.getTid(username);
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<TeacherCourse> data = teacherCourseMapper.findPersonalPage(pageNum, pageSize, cid,
                roomName, username, cname, mode, now);
        for (TeacherCourse msg : data)
        {
            if (DateUtil.compare(now, msg.getStartDate()) < 0) msg.setMode("待使用");
            if (DateUtil.compare(now, msg.getStartDate()) >= 0 && DateUtil.compare(now, msg.getEndDate()) <= 0)
                msg.setMode("在使用");
            if (DateUtil.compare(now, msg.getEndDate()) > 0) msg.setMode("已使用");
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = teacherCourseMapper.countPersonalTotal(cid, roomName, tid, cname, mode, now);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = true,isolation = Isolation.REPEATABLE_READ)
    public Result findApproval(Integer pageNum, Integer pageSize, String roomName, String username, String cid, String cname, String state)
    {
        String tid=teacherCourseMapper.getTid(username);
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<TeacherCourse> data = teacherCourseMapper.findApproval(pageNum, pageSize, cid,
                roomName, tid, cname, state);
        for (TeacherCourse msg : data)
        {
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = teacherCourseMapper.countApproval(cid, roomName, tid, cname, state);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }


    public Result handleRefuse(TeacherCourse tc)
    {
        // ①检查该信息是否已经被审批
        if (! Objects.equals(tc.getState(), "待批准"))
            return Result.error("500", "该信息已被处理");
        System.out.println(tc.getRemark());
        teacherCourseMapper.refuse(tc.getTcid(),tc.getRemark());
        teacherCourseMapper.sendMessage(tc.getAdmin(),tc.getUsername(),"您为申请的课程号为："+tc.getCid()+" 的教室 已被拒绝 "+tc.getRemark(),DateUtil.date());
        asignService.tryBatchReassign(0);
        return Result.success("success");
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result handleAddStudent(StudentCourseDTO sc)
    {
        String sid=sc.getSid();
        String studentName=sc.getStudentName();
        String cid=sc.getCid();
        if(StrUtil.isBlank(sid)||StrUtil.isBlank(studentName)){
            return Result.error("500","未输入学号或学生姓名");
        }
        String checkTcid = teacherCourseMapper.checkCid(cid);
        if(StrUtil.isBlank(checkTcid)){
            return Result.error("500","该课序号不存在");
        }
        if(! Objects.equals(teacherCourseMapper.findStudentName(sid), studentName)){
            return Result.error("500","学号与学生姓名不匹配");
        }

        String checkAble = teacherCourseMapper.checkAble(cid);
        if(StrUtil.isBlank(checkAble)){
            return Result.error("500","该课程尚未有教师授课");
        }

        String checkRepeat=teacherCourseMapper.checkRepeat(sid,cid);
        if(StrUtil.isNotBlank(checkRepeat))
            return Result.error("500","该学生已经加入该课程");
        int res=teacherCourseMapper.handleAddStudent(sid,cid);
        return Result.success("success");
    }
}

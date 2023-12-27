package com.mgl.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.entity.Student;
import com.mgl.server.entity.StudentCourse;
import com.mgl.server.entity.TeacherCourse;
import com.mgl.server.mapper.StudentCourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentCourseService
{
    @Autowired
    StudentCourseMapper studentCourseMapper;
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

    @Transactional(readOnly = true)
    public Result findPage(Integer pageNum, Integer pageSize, String cid, String name, String cname,String sid)
    {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<StudentCourse> data = studentCourseMapper.findPage(pageNum, pageSize, cid,"", name, cname, sid);
        int total = studentCourseMapper.countTotal(cid,"", name, cname, sid);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    public Result deleteBySidAndTcid(StudentCourseDTO sc)
    {
        int res = studentCourseMapper.deleteBySidAndTcid(sc.getSid(),sc.getCid());
        if(res>0)
            return Result.success("success");
        else return Result.error("500","删除失败");
    }

    public Result delBatch(List<StudentCourseDTO> list)
    {
        int ans=0;
        for(StudentCourseDTO sc:list){
            ans+=studentCourseMapper.deleteBySidAndTcid(sc.getSid(),sc.getCid());
        }
        return Result.success(ans);
    }

    public Result findStudentPage(Integer pageNum, Integer pageSize, String cid, String roomName, String name, String cname, String mode,String username)
    {
        pageNum = (pageNum - 1) * pageSize;
        String sid = studentCourseMapper.getSid(username);
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<StudentCourse> data = studentCourseMapper.findPage(pageNum, pageSize, cid,roomName,
                name, cname,sid);
        for (StudentCourse msg : data)
        {
            if (DateUtil.compare(now, msg.getStartDate()) < 0) msg.setMode("待使用");
            if (DateUtil.compare(now, msg.getStartDate()) >= 0 && DateUtil.compare(now, msg.getEndDate()) <= 0)
                msg.setMode("在使用");
            if (DateUtil.compare(now, msg.getEndDate()) > 0) msg.setMode("已使用");
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = studentCourseMapper.countTotal(cid,roomName, name, cname,sid);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }
}

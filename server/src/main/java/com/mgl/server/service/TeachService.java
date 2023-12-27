package com.mgl.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.controller.dto.TeachDTO;
import com.mgl.server.entity.StudentCourse;
import com.mgl.server.mapper.TeachMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TeachService
{
    @Autowired
    TeachMapper teachMapper;

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
    public Result findPage(Integer pageNum, Integer pageSize, String cid, String name, String cname)
    {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<StudentCourse> data = teachMapper.findPage(pageNum, pageSize, cid,"", name, cname,"");
        int total = teachMapper.countTotal(cid,"", name, cname,"");
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    public Result deleteByTidAndTcid(TeachDTO t)
    {
        int res = teachMapper.deleteByTidAndTcid(t.getTid(),t.getCid());
        if(res>0)
            return Result.success("success");
        else return Result.error("500","删除失败");
    }

    public Result delBatch(List<TeachDTO> list)
    {
        int ans=0;
        for(TeachDTO sc:list){
            ans+=teachMapper.deleteByTidAndTcid(sc.getTid(),sc.getCid());
        }
        return Result.success(ans);
    }

    public Result handleAddStudent(TeachDTO tc)
    {
        String tid=tc.getTid();
        String cid=tc.getCid();
        if(StrUtil.isBlank(tid)){
            return Result.error("500","未输入教工号");
        }

        String checkTid = teachMapper.checkTid(tid);
        if(StrUtil.isBlank(checkTid)){
            return Result.error("500","该教工号不存在");
        }

        String checkCid = teachMapper.checkCid(cid);
        if(StrUtil.isBlank(checkCid)){
            return Result.error("500","该课序号不存在");
        }

        String checkRepeat=teachMapper.checkRepeat(cid);
        if(StrUtil.isNotBlank(checkRepeat))
            return Result.error("500","该课程已有教师教授");
        int res=teachMapper.handleAddTeacher(tid,cid);
        return Result.success("success");
    }

    public Result findStudentPage(Integer pageNum, Integer pageSize, String cid, String roomName, String cname, String mode, String username)
    {
        pageNum = (pageNum - 1) * pageSize;
        String tid = teachMapper.getTid(username);
        Map<String, Object> res = new HashMap<>();
        DateTime now = new DateTime(Calendar.getInstance());
        List<StudentCourse> data = teachMapper.findPage(pageNum, pageSize, cid,roomName, "", cname,tid);
        for (StudentCourse msg : data)
        {
            if (DateUtil.compare(now, msg.getStartDate()) < 0) msg.setMode("待使用");
            if (DateUtil.compare(now, msg.getStartDate()) >= 0 && DateUtil.compare(now, msg.getEndDate()) <= 0)
                msg.setMode("在使用");
            if (DateUtil.compare(now, msg.getEndDate()) > 0) msg.setMode("已使用");
            msg.setStrWeekday(Trans(msg.getWeekday()));
        }
        int total = teachMapper.countTotal(cid,"",roomName, cname,tid);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }
}

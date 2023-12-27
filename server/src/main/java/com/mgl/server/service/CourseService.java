package com.mgl.server.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Tools;
import com.mgl.server.common.Result;
import com.mgl.server.entity.Course;
import com.mgl.server.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService
{
    @Autowired
    CourseMapper courseMapper;

    @Autowired
    TeacherCourseService teacherCourseService;

    @Transactional(readOnly = true)
    public Result findPage(Integer pageNum, Integer pageSize, String cid, String cname, String Credit, String ctype)
    {
        double credit;
        if(StrUtil.isBlank(Credit)||(! NumberUtil.isDouble(Credit)&&! NumberUtil.isInteger(Credit)))
            credit = 0;
        else credit=Double.parseDouble(Credit);
        pageNum = (pageNum - 1) * pageSize;
        Map<String,Object> res = new HashMap<>();
        List<Course> data = courseMapper.findPage(pageNum,pageSize,cid,cname,credit,ctype);
        int total = courseMapper.countTotal(pageNum,pageSize,cid,cname,credit,ctype);
        res.put("data",data);
        res.put("total",total);
        return Result.success(res);
    }

    @Transactional(readOnly =false,isolation = Isolation.REPEATABLE_READ)
    public Result save(Course course)
    {
        if(StrUtil.isBlank(course.getCid())){
            return Result.error("500","未输入课程编号");
        }
        if(StrUtil.isNotBlank(courseMapper.checkRepeat(course.getCid()))){
            return Result.error("500","该课程号已存在");
        }
        int saveRes = courseMapper.save(course);
        return Result.success(saveRes);
    }

    @Transactional(readOnly =false,isolation = Isolation.REPEATABLE_READ)
    public Result update(Course course)
    {
        if(StrUtil.isBlank(course.getCid())){
            return Result.error("500","未输入课程编号");
        }
        if(StrUtil.isBlank(courseMapper.checkRepeat(course.getCid()))){
            return Result.error("500","该课程号不存在");
        }
        Tools.cout(course.getCid());
        int updateRes=courseMapper.update(course);
        return Result.success(updateRes);
    }

    @Transactional(readOnly =false,isolation = Isolation.REPEATABLE_READ)
    public Result delete(String cid)
    {
        if(StrUtil.isBlank(cid)){
            return Result.error("500","未输入课程编号");
        }
        if(StrUtil.isBlank(courseMapper.checkRepeat(cid))){
            return Result.error("500","该课程号不存在");
        }

        int tcid = courseMapper.getTicd(cid);
        teacherCourseService.delete(tcid);

        int deleteRes = courseMapper.delete(cid);
        return Result.success(deleteRes);
    }

    @Transactional(readOnly =false,isolation = Isolation.REPEATABLE_READ)
    public Result delBatch(List<String> cids)
    {
        int total=0;
        for(String cid : cids){
            total += courseMapper.delete(cid);
        }
        return Result.success(total);
    }

    @Transactional(readOnly =true,isolation = Isolation.REPEATABLE_READ)
    public List<Course> list()
    {
        return courseMapper.list();
    }
}

package com.mgl.server.service;

import com.mgl.server.common.Result;
import com.mgl.server.entity.Student;
import com.mgl.server.entity.Teacher;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.TeacherMapper;
import com.mgl.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService
{
    @Autowired
    TeacherMapper teacherMapper;
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    public Map<String, Object> findPage(Integer pageNum,
                                        Integer pageSize,
                                        String tid,
                                        String name,
                                        String username)
    {
        Map<String, Object> res = new HashMap<>();
        // limit 第一个参数为 (pageNum-1)*pageSize, 第二个参数为pageSize
        pageNum = (pageNum - 1) * pageSize;
        List<Teacher> data = teacherMapper.findPage(pageNum, pageSize, tid, name, username);
        int total = teacherMapper.countTotal(tid, name, username);

        res.put("data", data);
        res.put("total", total);
        return res;
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result save(Teacher teacher)
    {
        if(teacherMapper.mapperTid(teacher.getTid())==null){
            User user = new User(teacher.getTid(),null,teacher.getName(),"教师",teacher.getEmail(),teacher.getPhone());
            userService.save(user);
            teacher.setUsername(teacher.getTid());
            teacherMapper.save(teacher);
            return Result.success(true);
        }
        else{
            return Result.error("500","该教师已经存在");
        }
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result update(Teacher teacher)
    {
        if(teacherMapper.mapperTid(teacher.getTid())==null){
            return Result.error("500","该教师不存在");
        }
        User user = new User(teacher.getTid(),null,teacher.getName(),"教师",teacher.getEmail(),teacher.getPhone());
        userService.save(user);
        teacherMapper.update(teacher);
        return Result.success(true);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public int delete(String username)
    {
        return userMapper.deleteByUserName(username);
    }
}

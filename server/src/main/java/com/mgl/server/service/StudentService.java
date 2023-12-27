package com.mgl.server.service;

import com.mgl.server.common.Result;
import com.mgl.server.entity.Student;
import com.mgl.server.entity.User;
import com.mgl.server.mapper.StudentMapper;
import com.mgl.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService
{
    @Autowired
    StudentMapper studentMapper;
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    public Map<String, Object> findPage(Integer pageNum,
                                        Integer pageSize,
                                        String sid,
                                        String name,
                                        String username)
    {
        Map<String,Object> res= new HashMap<>();
        pageNum = (pageNum - 1) * pageSize;
        List<Student> data=studentMapper.findPage(pageNum,pageSize,sid,name,username);
        int total=studentMapper.countTotal(pageNum,pageSize,sid,name,username);
        System.out.println(data);
        res.put("data",data);
        res.put("total",total);
        System.out.println("--------------------");
        System.out.println(res);
        return res;
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result save(Student student)
    {
        if(studentMapper.mapperSid(student.getSid())==null){
            User user = new User(student.getSid(),null,student.getName(),"学生",student.getEmail(),student.getPhone());
            userService.save(user);
            student.setUsername(student.getSid());
            studentMapper.save(student);
            return Result.success(true);
        }
        else{
            return Result.error("500","该学号已经存在");
        }
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result update(Student student)
    {
        if(studentMapper.mapperSid(student.getSid())==null){
            return Result.error("500","该学生不存在");
        }
        User user = new User(student.getSid(),null,student.getName(),"学生",student.getEmail(),student.getPhone());
        userService.save(user);
        studentMapper.update(student);
        return Result.success(true);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public int delete(String username)
    {
        return userMapper.deleteByUserName(username);
    }
}

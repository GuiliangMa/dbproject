package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.mapper.StudentMapper;
import com.mgl.server.service.StudentService;
import com.mgl.server.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentController
{
    @Autowired
    StudentService studentService;
    @Autowired
    StudentMapper studentMapper;

    @GetMapping("/page")
    public Map<String,Object> findPage(@RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam String sid,
                                       @RequestParam String name,
                                       @RequestParam String username)
    {
        return studentService.findPage(pageNum,pageSize,sid,name,username);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Student student){
        return studentService.save(student);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Student student){
        return studentService.update(student);
    }

    @DeleteMapping("/{username}")
    public int delete(@PathVariable String username){
        return studentService.delete(username);
    }
}

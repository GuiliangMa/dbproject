package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.entity.Student;
import com.mgl.server.entity.Teacher;
import com.mgl.server.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/teacher")
public class TeacherController
{
    @Autowired
    TeacherService teacherService;

    @GetMapping("/page")
    public Map<String,Object> findPage(@RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam String tid,
                                       @RequestParam String name,
                                       @RequestParam String username)
    {
        return teacherService.findPage(pageNum,pageSize,tid,name,username);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Teacher teacher){
        return teacherService.save(teacher);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Teacher teacher){
        return teacherService.update(teacher);
    }

    @DeleteMapping("/{username}")
    public int delete(@PathVariable String username){
        return teacherService.delete(username);
    }
}

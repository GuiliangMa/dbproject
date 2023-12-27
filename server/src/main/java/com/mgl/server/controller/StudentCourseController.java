package com.mgl.server.controller;

import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/studentcourse")
public class StudentCourseController
{
    @Autowired
    StudentCourseService studentCourseService;
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name,
                           @RequestParam String cname,
                           @RequestParam String cid,
                           @RequestParam String sid){
        return studentCourseService.findPage(pageNum,pageSize,cid,name,cname,sid);
    }

    @PostMapping("/delete")
    public Result deleteBySidAndTcid(@RequestBody StudentCourseDTO sc){
        return studentCourseService.deleteBySidAndTcid(sc);
    }

    @PostMapping("/delbatch")
    public Result delBatch(@RequestBody List<StudentCourseDTO> list){
        return studentCourseService.delBatch(list);
    }

    @GetMapping("/studentpage")
    public Result findStudentPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String name,
                           @RequestParam String cname,
                           @RequestParam String mode,
                           @RequestParam String cid,
                           @RequestParam String username){
        return studentCourseService.findStudentPage(pageNum,pageSize,cid,roomName,name,cname,mode,username);
    }
}

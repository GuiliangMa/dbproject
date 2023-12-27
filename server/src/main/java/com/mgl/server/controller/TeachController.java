package com.mgl.server.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.controller.dto.TeachDTO;
import com.mgl.server.service.TeachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/teach")
public class TeachController
{
    @Autowired
    TeachService teachService;

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name,
                           @RequestParam String cname,
                           @RequestParam String cid){
        return teachService.findPage(pageNum,pageSize,cid,name,cname);
    }

    @PostMapping("/delete")
    public Result deleteBySidAndTcid(@RequestBody TeachDTO t){
        return teachService.deleteByTidAndTcid(t);
    }

    @PostMapping("/delbatch")
    public Result delBatch(@RequestBody List<TeachDTO> list){
        return teachService.delBatch(list);
    }

    @PostMapping("/handleAdd")
    public Result handleAddStudent(@RequestBody TeachDTO tc){
        return teachService.handleAddStudent(tc);
    }

    @PostMapping("/importStudentFile")
    public Boolean importStudentFile(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<TeachDTO> list=reader.readAll(TeachDTO.class);
        int total = 0;
        for(TeachDTO tc:list){

            Result res = teachService.handleAddStudent(tc);
            if(Objects.equals(res.getCode(), "200")) total++;
        }
        System.out.println("~~~~~~");
        System.out.println(total);
        System.out.println("~~~~~~");
        return total > 0;
    }

    @GetMapping("/teachpage")
    public Result findStudentPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam String roomName,
                                  @RequestParam String cname,
                                  @RequestParam String mode,
                                  @RequestParam String cid,
                                  @RequestParam String username){
        return teachService.findStudentPage(pageNum,pageSize,cid,roomName,cname,mode,username);
    }
}

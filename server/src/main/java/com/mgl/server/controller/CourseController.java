package com.mgl.server.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.Course;
import com.mgl.server.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/course")
public class CourseController
{
    @Autowired
    CourseService courseService;
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String cid,
                           @RequestParam String cname,
                           @RequestParam String credit,
                           @RequestParam String ctype){
        return courseService.findPage(pageNum,pageSize,cid,cname,credit,ctype);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Course course){
        return courseService.save(course);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Course course){
        return courseService.update(course);
    }

    @DeleteMapping("/{cid}")
    public Result delete(@PathVariable String cid){
        return courseService.delete(cid);
    }

    @PostMapping("/delbatch")
    public Result delBatch(@RequestBody List<String> cids){
        return courseService.delBatch(cids);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        //System.out.println(filePath);
        List<Course> list=courseService.list();

        // 通过hutool的 ExcelWrite类创建 writer，在getWriter中可以指定磁盘路径
        ExcelWriter writer= ExcelUtil.getWriter(true);
        // 自定义别名
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","电子邮箱");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("create_time","创建时间");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list,true);
        // 设置浏览器的响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("课程信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out =response.getOutputStream();
        writer.flush(out,true);//刷新至输出流
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public Result impo(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader =ExcelUtil.getReader(inputStream);
        List<Course> list=reader.readAll(Course.class);
        int ans=0;
        for(Course course:list){
            if(Objects.equals(courseService.save(course).getCode(), "200"))
                ans++;
        }
        return Result.success(ans);
    }
}

package com.mgl.server.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.controller.dto.TeacherCourseDTO;
import com.mgl.server.controller.dto.iDTO;
import com.mgl.server.entity.TeacherCourse;
import com.mgl.server.service.TeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/teachercourse")
public class TeacherCourseController
{
    @Autowired
    TeacherCourseService teacherCourseService;

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String cname,
                           @RequestParam String mode,
                           @RequestParam String cid){
        return teacherCourseService.findPage(pageNum,pageSize,cid,roomName,cname,mode);
    }

    @DeleteMapping("/{tcid}")
    public Result delete(@PathVariable int tcid){
        return teacherCourseService.delete(tcid);
    }

    @Transactional(readOnly = false,isolation = Isolation.SERIALIZABLE)
    @PostMapping("/delbatch")
    public Result delBatch(@RequestBody List<Integer> tcids){
        int ans=0;
        for(Integer tcid : tcids){
            Result res = teacherCourseService.delete(tcid);
            if(Objects.equals(res.getCode(), "200")) ans++;
        }
        return Result.success(ans);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        //System.out.println(filePath);
        List<TeacherCourse> list=teacherCourseService.list();

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
        String fileName = URLEncoder.encode("课程教室信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out =response.getOutputStream();
        writer.flush(out,true);//刷新至输出流
        out.close();
        writer.close();
    }

    @GetMapping("/findroom")
    public Result findRoom(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String startDate,
                           @RequestParam String endDate,
                           @RequestParam String weekday,
                           @RequestParam String beginTime,
                           @RequestParam String endTime,
                           @RequestParam Integer times){
        return teacherCourseService.findRoom(pageNum,pageSize,startDate,endDate,weekday,beginTime,endTime,times);
    }

//    @GetMapping("/gettid")
//    public Result getTid(@RequestParam String username){
//        return teacherCourseService.getTid(username);
//    }

    @PostMapping("/apply")
    public Result apply(@RequestBody TeacherCourseDTO teacherCourseDTO)
    {
        return teacherCourseService.apply(teacherCourseDTO);
    }

    @GetMapping("/trial")
    public Result findTrialMessgae(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize,
                                   @RequestParam String roomName,
                                   @RequestParam String username,
                                   @RequestParam String cid,
                                   @RequestParam String cname,
                                   @RequestParam String state){
        return teacherCourseService.findTrialMessage(pageNum,pageSize,roomName,username,cid,cname,state);
    }

    @PostMapping("/accept")
    public Result accept(@RequestBody iDTO dto){
        return teacherCourseService.accept(dto);
    }

    @GetMapping("/personal")
    public Result findPersonalPage(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize,
                                   @RequestParam String roomName,
                                   @RequestParam String username,
                                   @RequestParam String cid,
                                   @RequestParam String cname,
                                   @RequestParam String mode){
        return teacherCourseService.findPersonalPage(pageNum,pageSize,roomName,username,cid,cname,mode);
    }

    @GetMapping("/approval")
    public Result findApproval(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String roomName,
                               @RequestParam String username,
                               @RequestParam String cid,
                               @RequestParam String cname,
                               @RequestParam String state){
        return teacherCourseService.findApproval(pageNum,pageSize,roomName,username,cid,cname,state);
    }

    @PostMapping("/refuse")
    public Result handleRefuse(@RequestBody TeacherCourse tc){
        return teacherCourseService.handleRefuse(tc);
    }

    @PostMapping("/handleAdd")
    public Result handleAddStudent(@RequestBody StudentCourseDTO sc){
        return teacherCourseService.handleAddStudent(sc);
    }


    @PostMapping("/importStudentFile")
    public Boolean importStudentFile(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader =ExcelUtil.getReader(inputStream);
        List<StudentCourseDTO> list=reader.readAll(StudentCourseDTO.class);
        int total = 0;
        for(StudentCourseDTO sc:list){

            Result res = teacherCourseService.handleAddStudent(sc);
            if(Objects.equals(res.getCode(), "200")) total++;
        }
        System.out.println("~~~~~~");
        System.out.println(total);
        System.out.println("~~~~~~");
        return total > 0;
    }
}

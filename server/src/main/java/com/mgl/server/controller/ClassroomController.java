package com.mgl.server.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.Student;
import com.mgl.server.entity.User;
import com.mgl.server.service.ClassroomService;
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
import java.util.Map;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/classroom")
public class ClassroomController
{
    @Autowired
    ClassroomService classroomService;
    @GetMapping("/page")
    public Map<String,Object> findPage(@RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam String roomName,
                                       @RequestParam Integer capacity,
                                       @RequestParam Integer multiMedia,
                                       @RequestParam Integer AC,
                                       @RequestParam String type){
        return classroomService.findPage(pageNum,pageSize,roomName,capacity,multiMedia,AC,type);
    }

    @PostMapping("/save")
    public Result save(@RequestBody ClassroomDTO classroomDTO){
        return classroomService.save(classroomDTO);
    }

    @PostMapping("/update")
    public Result update(@RequestBody ClassroomDTO classroomDTO){
        return classroomService.update(classroomDTO);
        // return classroomService.update(classroomDTO);
    }

    @DeleteMapping("/{rid}")
    public Integer delete(@PathVariable int rid){
        return classroomService.delete(rid);
    }

    @PostMapping("/delbatch")
    public Integer delbatch(@RequestBody List<Integer> classrooms){
        return classroomService.delBatch(classrooms);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        //System.out.println(filePath);
        List<Classroom> list=classroomService.list();

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
        String fileName = URLEncoder.encode("教室信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out =response.getOutputStream();
        writer.flush(out,true);//刷新至输出流
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public Boolean impo(MultipartFile file) throws Exception{
        System.out.println("!");
        InputStream inputStream = file.getInputStream();
        ExcelReader reader =ExcelUtil.getReader(inputStream);
        List<Classroom> list=reader.readAll(Classroom.class);
        int ans=0;
        for(Classroom classroom:list){
            if(Objects.equals(classroomService.save(new ClassroomDTO(classroom)).getCode(), "200"))
                ans++;
        }
        return ans > 0;
    }
}

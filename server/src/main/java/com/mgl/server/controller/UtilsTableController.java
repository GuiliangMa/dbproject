package com.mgl.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mgl.server.common.Result;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.UtilsTable;
import com.mgl.server.service.UtilsTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/usage")
public class UtilsTableController
{
    @Autowired
    UtilsTableService utilsTableService;

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String name,
                           @RequestParam String status,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam String state,
                           @RequestParam String props){
        return utilsTableService.findPage(pageNum,pageSize,roomName,name,status,startTime,endTime,state,props);
    }

    @DeleteMapping("/{uid}")
    public int delete(@PathVariable int uid){
        return utilsTableService.delete(uid);
    }

    @PostMapping("/delbatch")
    public Integer deleteBatch(@RequestBody List<Integer> uids){
        return utilsTableService.deleteBatch(uids);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        List<UtilsTable> list=utilsTableService.list();
        ExcelWriter writer= ExcelUtil.getWriter(true);
        writer.write(list,true);
        // 设置浏览器的响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("教室使用信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out =response.getOutputStream();
        writer.flush(out,true);//刷新至输出流
        out.close();
        writer.close();
    }

    @GetMapping("/mypage")
    public Result findMyPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String roomName,
                           @RequestParam String username,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam String state){
        return utilsTableService.findMyPage(pageNum,pageSize,roomName,username,startTime,endTime,state);
    }
}

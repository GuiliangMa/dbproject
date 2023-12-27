package com.mgl.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.controller.dto.UtilsTableDTO;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.UtilsTable;
import com.mgl.server.mapper.UtilsTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
public class UtilsTableService
{
    @Autowired
    UtilsTableMapper utilsTableMapper;

    @Autowired
    AsignService asignService;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Result findPage(Integer pageNum,
                           Integer pageSize,
                           String roomName,
                           String name,
                           String status,
                           String StartTime,
                           String EndTime,
                           String state,
                           String props)
    {
        Date startTime;
        if (StrUtil.isBlank(StartTime))
            startTime = DateUtil.parse("1970-01-01");
        else startTime = DateUtil.parse(StartTime);

        Date endTime;
        if (StrUtil.isBlank(EndTime))
            endTime = DateUtil.parse("2099-12-31");
        else endTime = DateUtil.parse(EndTime);
        pageNum = (pageNum - 1) * pageSize;
        if (startTime.compareTo(endTime) >= 0)
        {
            return Result.error("500", "未正确输入启用和终止时间");
        }

        DateTime now = new DateTime(Calendar.getInstance());
//        System.out.println("~~~~~~~~~~");
//        System.out.println(state);
//        System.out.println(now);
//        System.out.println("~~~~~~~~~~");

        Map<String, Object> res = new HashMap<>();
        List<UtilsTableDTO> temp = utilsTableMapper.findPage(pageNum, pageSize,
                startTime, endTime, roomName, name, status, state, now,props);

        List<UtilsTable> data = new ArrayList<>();

        for (UtilsTableDTO utilDTO : temp)
        {
//            String status = utilDTO.getStatus();
//            if (Objects.equals(status, "学生"))
//            {
//                utilDTO.setName(utilsTableMapper.getStudentNameByUsername(utilDTO.getUsername()));
//            }
//            if (Objects.equals(status, "教师"))
//            {
//                utilDTO.setName(utilsTableMapper.getTeacherNameByUsername(utilDTO.getUsername()));
//            }
//            if (Objects.equals(status, "管理员"))
//            {
//                utilDTO.setName("管理员");
//            }
            data.add(new UtilsTable(utilDTO));
        }
        int total = utilsTableMapper.countTotal(startTime, endTime, roomName, name, status, state, now,props);
//        System.out.println("----------");
//        System.out.println(data);
//        System.out.println("----------");
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    public int delete(int uid)
    {
        int asgid=utilsTableMapper.getAsgid(uid);
        if(asgid!=0){
            asignService.drop(asgid);
        }
        return utilsTableMapper.deleteById(uid);
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    public Integer deleteBatch(List<Integer> uids)
    {
        int ans = 0;
        for (int uid : uids)
        {
            ans += delete(uid);
        }
        return ans;
    }

    @Transactional(readOnly = true)
    public List<UtilsTable> list()
    {
        List<UtilsTableDTO> temp = utilsTableMapper.list();
        List<UtilsTable> data = new ArrayList<>();
        for (UtilsTableDTO msg : temp)
        {
            data.add(new UtilsTable(msg));
        }
        return data;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Result findMyPage(Integer pageNum, Integer pageSize, String roomName, String username, String StartTime, String EndTime, String state)
    {
        Date startTime;
        if (StrUtil.isBlank(StartTime))
            startTime = DateUtil.parse("1970-01-01");
        else startTime = DateUtil.parse(StartTime);

        Date endTime;
        if (StrUtil.isBlank(EndTime))
            endTime = DateUtil.parse("2099-12-31");
        else endTime = DateUtil.parse(EndTime);
        pageNum = (pageNum - 1) * pageSize;
        if (startTime.compareTo(endTime) >= 0)
        {
            return Result.error("500", "未正确输入启用和终止时间");
        }

        DateTime now = new DateTime(Calendar.getInstance());
//        System.out.println("~~~~~~~~~~");
//        System.out.println(state);
//        System.out.println(now);
//        System.out.println("~~~~~~~~~~");

        Map<String, Object> res = new HashMap<>();
        List<UtilsTableDTO> temp = utilsTableMapper.findMyPage(pageNum, pageSize,
                startTime, endTime, roomName, username, state, now);

        List<UtilsTable> data = new ArrayList<>();

        for (UtilsTableDTO utilDTO : temp)
        {
            data.add(new UtilsTable(utilDTO));
        }
        int total = utilsTableMapper.countMyTotal(startTime, endTime, roomName, username, state, now);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }
}

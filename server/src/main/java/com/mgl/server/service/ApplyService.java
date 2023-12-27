package com.mgl.server.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.common.Tools;
import com.mgl.server.controller.dto.ApplyDTO;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.controller.dto.UtilsTableDTO;
import com.mgl.server.entity.Apply;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.UtilsTable;
import com.mgl.server.mapper.ApplyMapper;
import com.mgl.server.mapper.UtilsTableMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ApplyService
{
    @Autowired
    ApplyMapper applyMapper;
    @Autowired
    UtilsTableMapper utilsTableMapper;

    @Autowired
    AsignService asignService;

    @Transactional(readOnly = true)
    public Result findFreePage(Integer pageNum,
                               Integer pageSize,
                               String useDate,
                               String StartTime,
                               String EndTime,
                               String roomName,
                               Integer capacity,
                               Integer multiMedia,
                               Integer AC,
                               String type)
    {
        if(StrUtil.isBlank(StartTime)||StrUtil.isBlank(EndTime)||StrUtil.isBlank(useDate)){
            return Result.error("500","未输入使用时间");
        }
        pageNum = (pageNum - 1) * pageSize;
        Map<String,Object> res = new HashMap<>();
        Date startTime = DateUtil.parse(Tools.dateFormat(useDate)+" "+StartTime);
        Date endTime =DateUtil.parse(Tools.dateFormat(useDate)+" "+EndTime);
        if(startTime.compareTo(endTime)>=0){
            return Result.error("500","未正确输入启用和终止时间");
        }
        List<Classroom> temp = applyMapper.findFreePage(pageNum,pageSize,startTime,endTime,
                roomName,capacity,multiMedia,AC,type);
        List<ClassroomDTO> data = new ArrayList<>();
        for(Classroom classroom : temp){
            data.add(new ClassroomDTO(classroom));
        }
        int total = applyMapper.totalFree(startTime,endTime,roomName,capacity,multiMedia,AC,type);
        res.put("data",data);
        res.put("total",total);
        return Result.success(res);
    }

    @Transactional(readOnly = true)
    public Result getIdAndName(String username)
    {
        Map<String,Object> data = applyMapper.getIdAndName(username);
        return Result.success(data);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result applyRoom(ApplyDTO applyDTO)
    {
        applyDTO.setStartTime(DateUtil.parse(Tools.dateFormat(applyDTO.getDate())+" "+applyDTO.getBegin()));
        applyDTO.setEndTime(DateUtil.parse(Tools.dateFormat(applyDTO.getDate())+" "+applyDTO.getEnd()));
        String checkAvailable = applyMapper.checkAvailabel(applyDTO);
        if(StrUtil.isNotBlank(checkAvailable)){
            return Result.error("500","您申请的教室已被占用");
        }
        String checkRepeat = applyMapper.checkRepeat(applyDTO);
        if(StrUtil.isNotBlank(checkRepeat)){
            return Result.error("500","您已经申请过该时间段");
        }
        int res = applyMapper.apply(applyDTO);
        if(res == 1)
            return Result.success(1);
        else return Result.error("500","申请失败");
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    public Result findPage(Integer pageNum, Integer pageSize,
                           String roomName, String name, String status,
                           String StartTime, String EndTime, String mode)
    {
        Date startTime;
        if(StrUtil.isBlank(StartTime))
            startTime = DateUtil.parse("1970-01-01");
        else startTime = DateUtil.parse(StartTime);

        Date endTime;
        if(StrUtil.isBlank(EndTime))
            endTime = DateUtil.parse("2099-12-31");
        else endTime = DateUtil.parse(EndTime);
        pageNum = (pageNum - 1) * pageSize;

        if(startTime.compareTo(endTime)>=0){
            return Result.error("500","未正确输入启用和终止时间");
        }

        Map<String, Object> res = new HashMap<>();
        List<ApplyDTO> temp = applyMapper.findPage(pageNum,pageSize,
                startTime,endTime,roomName,name,status,mode);

        List<Apply> data = new ArrayList<>();

        for (ApplyDTO applyDTO : temp)
        {
            data.add(new Apply(applyDTO));
        }
        int total = applyMapper.countTotal(startTime,endTime,roomName,name,status,mode);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public Result accepte(@NotNull Apply apply)
    {
//        System.out.println("~~~~~~");
//        System.out.println(apply.getAdmin());
//        System.out.println("~~~~~~~~");
        // ①检索该申请是否仍为待批准
        String nowMode = applyMapper.findMode(apply.getApid());
        if(!StrUtil.equals(nowMode,"待批准")){
            return Result.error("500","该信息已被处理");
        }
        // ②修改其为已同意
        int update = applyMapper.accept(apply.getApid());
        // ③修改使用表
        utilsTableMapper.insert(apply.getUsername(),apply.getRid(),
                apply.getStartTime(),apply.getEndTime(),apply.getRemark(),"临时");
        // ④将其他与该时段冲突的申请设为拒绝
        // applyMapper.modify(apply.getRid(),apply.getStartTime(),apply.getEndTime());
        List<ApplyDTO> temp =applyMapper.modifyList(apply.getRid(),apply.getStartTime(),apply.getEndTime());
        for(ApplyDTO ap:temp)
        {
            applyMapper.refuseByApid(ap.getApid(),"该时段此教室被其他人占用");
            applyMapper.sendMessage(apply.getAdmin(),ap.getUsername(),"您申请的"+ap.getRoomName()+"教室，已拒绝 "+" 该时段此教室被其他人占用",DateUtil.date());
        }

        applyMapper.sendMessage(apply.getAdmin(),apply.getUsername(),"您申请的"+apply.getRoomName()+"教室，已同意",DateUtil.date());
        asignService.tryBatchReassign(0);

        return Result.success(update);
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    public Result findMyPage(Integer pageNum, Integer pageSize, String roomName, String username, String StartTime, String EndTime, String mode)
    {
        Date startTime;
        if(StrUtil.isBlank(StartTime))
            startTime = DateUtil.parse("1970-01-01");
        else startTime = DateUtil.parse(StartTime);

        Date endTime;
        if(StrUtil.isBlank(EndTime))
            endTime = DateUtil.parse("2099-12-31");
        else endTime = DateUtil.parse(EndTime);
        pageNum = (pageNum - 1) * pageSize;

        if(startTime.compareTo(endTime)>=0){
            return Result.error("500","未正确输入启用和终止时间");
        }

        Map<String, Object> res = new HashMap<>();
        List<ApplyDTO> temp = applyMapper.findMyPage(pageNum,pageSize,
                startTime,endTime,roomName,username,mode);

        List<Apply> data = new ArrayList<>();

        for (ApplyDTO applyDTO : temp)
        {
            data.add(new Apply(applyDTO));
        }
        int total = applyMapper.countMyTotal(startTime,endTime,roomName,username,mode);
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public Result refuse(Apply apply)
    {
        // ①检索该申请是否仍为待批准
        String nowMode = applyMapper.findMode(apply.getApid());
        if(!StrUtil.equals(nowMode,"待批准")){
            return Result.error("500","该信息已被处理");
        }
        // ②修改其为已拒绝
        int res = applyMapper.refuse(apply);
        applyMapper.sendMessage(apply.getAdmin(),apply.getUsername(),"您申请的"+apply.getRoomName()+"教室，已拒绝 "+apply.getMsg(),DateUtil.date());
        asignService.tryBatchReassign(0);
        return Result.success(res);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result revoke(Integer apid)
    {
        String state = applyMapper.findState(apid);
        if(! Objects.equals(state, "待批准"))
            return Result.error("500","该信息已被处理");
        applyMapper.revoke(apid);
        asignService.tryBatchReassign(0);
        return Result.success("success");
    }
}

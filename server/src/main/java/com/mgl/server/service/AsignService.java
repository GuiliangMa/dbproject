package com.mgl.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.common.Tools;
import com.mgl.server.controller.dto.AsignDTO;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.controller.dto.UtilsTableDTO;
import com.mgl.server.entity.Asign;
import com.mgl.server.entity.Classroom;
import com.mgl.server.entity.UtilsTable;
import com.mgl.server.mapper.AsignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AsignService
{
    @Autowired
    AsignMapper asignMapper;

    @Transactional(readOnly = false,isolation = Isolation.SERIALIZABLE)
    public Boolean tryReassign(int asgid){
        AsignDTO assign = asignMapper.getEntity(asgid);
        if(! Objects.equals(assign.getState(), "未分配"))
            return false;
        DateTime startTime=new DateTime(assign.getStartTime());
        DateTime endTime=new DateTime(assign.getEndTime());
        List<ClassroomDTO> list = asignMapper.findList(startTime,endTime);

        int newid=0;

        for(ClassroomDTO room :list){
            if(room.getCapacity()>=assign.getCapacity()&&
                    Objects.equals(room.getMultiMedia(), assign.getMultiMedia())&&
                    Objects.equals(room.getAC(), assign.getAc())&&
                    Objects.equals(room.getType(), assign.getType())){
                newid=Integer.parseInt(room.getRid());
                break;
            }
        }

        if(newid==0) return false;

        asignMapper.reassign(newid,assign.getUsername(),startTime,endTime,assign.getRemark(),"临时",asgid);
        asignMapper.success(asgid);
        asignMapper.sendMessage("admin",assign.getUsername(),"您请求教室信息 "+asgid+" 现已经分配至教室编号 "+newid+"，请查看",DateUtil.date());

        return true;
    }

    @Transactional(readOnly = false,isolation = Isolation.SERIALIZABLE)
    public void tryBatchReassign(int old){

        Date now = DateUtil.date();
        List<Integer> passList=asignMapper.passList(now);
        for(int asgid:passList){
            asignMapper.updatepass(asgid);
            asignMapper.sendMessage("admin",asignMapper.getUsername(asgid),"您请求教室信息 "+asgid+" 现已经过期请查看",DateUtil.date());
        }

        List<Integer> assignList=asignMapper.List();
        for(int asgid:assignList){
            if(asgid==old) continue;
            tryReassign(asgid);
        }
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public void drop(int asgid){
        asignMapper.drop(asgid);
        asignMapper.sendMessage("admin",asignMapper.getUsername(asgid),"您请求教室信息 "+asgid+" 获得的教室，现已经被收回",DateUtil.date());
        tryBatchReassign(asgid);
    }

    @Transactional(readOnly = false,isolation = Isolation.SERIALIZABLE)
    public Result assign(AsignDTO as)
    {
        as.setStartTime(DateUtil.parse(Tools.dateFormat(as.getDate())+" "+as.getBegin()));
        as.setEndTime(DateUtil.parse(Tools.dateFormat(as.getDate())+" "+as.getEnd()));
        Asign ass = new Asign(as);
        int res = asignMapper.assign(ass);

        String asgid = asignMapper.getAsgid(as);
        tryReassign(Integer.parseInt(asgid));

        if(res == 1)
            return Result.success(1);
        else return Result.error("500","申请失败");
    }

    public Result findPage(Integer pageNum, Integer pageSize, String roomName, String username, String StartTime, String EndTime, String state)
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

        Map<String, Object> res = new HashMap<>();
        List<AsignDTO> temp = asignMapper.findPage(pageNum, pageSize,
                startTime, endTime, roomName, username, state);

        List<Asign> data = new ArrayList<>();

        for (AsignDTO utilDTO : temp)
        {
            data.add(new Asign(utilDTO));
        }
        int total = asignMapper.countTotal(startTime, endTime, roomName, username,  state);
//        System.out.println("----------");
//        System.out.println(data);
//        System.out.println("----------");
        res.put("data", data);
        res.put("total", total);
        return Result.success(res);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result delete(Integer asgid)
    {
        AsignDTO assign = asignMapper.getEntity(asgid);
        String state=assign.getState();
        if(Objects.equals(state, "已分配")){
            asignMapper.deleteUidByAsgid(asgid);
            asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被管理员删除",DateUtil.date());
            asignMapper.deleteAsgid(asgid);
            tryBatchReassign(asgid);
        }
        else{
            asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被管理员删除",DateUtil.date());
            asignMapper.deleteAsgid(asgid);
        }
        return Result.success("success");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result delBatch(List<Integer> asgids)
    {
        for(int asgid:asgids){
            AsignDTO assign = asignMapper.getEntity(asgid);
            String state=assign.getState();
            if(Objects.equals(state, "已分配")){
                asignMapper.deleteUidByAsgid(asgid);
                asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被管理员删除",DateUtil.date());
                asignMapper.deleteAsgid(asgid);
            }
            else{
                asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被管理员删除",DateUtil.date());
                asignMapper.deleteAsgid(asgid);
            }
        }
        tryBatchReassign(0);
        return Result.success(asgids.size());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result revoke(Integer asgid)
    {
        AsignDTO assign = asignMapper.getEntity(asgid);
        String state=assign.getState();
        if(Objects.equals(state, "已分配")){
            asignMapper.deleteUidByAsgid(asgid);
            asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被您撤回",DateUtil.date());
            asignMapper.deleteAsgid(asgid);
            tryBatchReassign(asgid);
        }
        else{
            asignMapper.sendMessage("admin",assign.getUsername(),"您的请求教室信息，信息号为 "+asgid+" 已被您撤回",DateUtil.date());
            asignMapper.deleteAsgid(asgid);
        }
        return Result.success("success");
    }
}

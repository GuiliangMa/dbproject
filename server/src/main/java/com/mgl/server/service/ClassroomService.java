package com.mgl.server.service;

import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.ClassroomDTO;
import com.mgl.server.entity.Classroom;
import com.mgl.server.mapper.ClassroomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClassroomService
{
    @Autowired
    ClassroomMapper classroomMapper;
    public Map<String, Object> findPage(Integer pageNum,
                                        Integer pageSize,
                                        String roomName,
                                        Integer capacity,
                                        Integer multiMedia,
                                        Integer AC,
                                        String type)
    {
        System.out.println(type);
        Map<String,Object> res= new HashMap<>();
        pageNum = (pageNum - 1) * pageSize;
        List<Classroom> temp=classroomMapper.findPage(pageNum,pageSize,roomName,capacity,multiMedia,AC,type);
        List<ClassroomDTO> data = new ArrayList<>();
        for(Classroom classroom : temp){
            data.add(new ClassroomDTO(classroom));
        }
        int total=classroomMapper.countTotal(roomName,capacity,multiMedia,AC,type);
        System.out.println(data);
        res.put("data",data);
        res.put("total",total);
        System.out.println("--------------------");
        System.out.println(res);
        return res;
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result save(ClassroomDTO classroomDTO)
    {
        System.out.println("---------");
        System.out.println(classroomDTO);
        System.out.println("---------");
        if (classroomDTO.getRid() != null && ! classroomDTO.getRid().equals("0"))
        {
            return Result.error("500","出现异常");
        }
        Classroom classroom = new Classroom(classroomDTO);
        if(Objects.equals(classroom.getRoomName(), ""))
            return Result.error("500","未输入教室名");
        String state = classroomMapper.findByName(classroom.getRoomName());
        if(Objects.equals(state, "1"))
            return Result.error("500","该教室名称已存在");
//        System.out.println("------------");
//        System.out.println(classroom);
        classroomMapper.save(classroom);
        return Result.success(classroom);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public Result update(ClassroomDTO classroomDTO)
    {
        Classroom classroom = new Classroom(classroomDTO);
        String passRoomName = classroomMapper.getNameById(classroom.getRid());
        if(! Objects.equals(passRoomName, classroom.getRoomName()))
        {
            String state = classroomMapper.findByName(classroom.getRoomName());
            if(Objects.equals(classroom.getRoomName(), ""))
                return Result.error("500","未输入教室名");
            if(! Objects.equals(state, null))
                return Result.error("500","该教室名称已存在");
        }
//        System.out.println("--------");
//        System.out.println(classroom.getLocation());
//        System.out.println("--------");
        classroomMapper.update(classroom);
        return Result.success(classroom);
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ)
    public int delete(Integer rid)
    {
        return classroomMapper.deleteByRid(rid);
    }

    public Integer delBatch(List<Integer> classrooms)
    {
        int ans = 0;
        for(int rid : classrooms){
            ans += classroomMapper.deleteByRid(rid);
        }
        return ans;
    }

    public List<Classroom> list()
    {
        return classroomMapper.list();
    }
}

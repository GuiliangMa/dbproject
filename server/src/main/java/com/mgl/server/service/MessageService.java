package com.mgl.server.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.mgl.server.common.Result;
import com.mgl.server.controller.dto.MessageDTO;
import com.mgl.server.entity.Course;
import com.mgl.server.entity.Message;
import com.mgl.server.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService
{
    @Autowired
    MessageMapper messageMapper;

    @Transactional(readOnly = true)
    public Result findPage(Integer pageNum, Integer pageSize,String username)
    {
        pageNum = (pageNum - 1) * pageSize;
        Map<String,Object> res = new HashMap<>();
        List<MessageDTO> temp = messageMapper.findPage(pageNum,pageSize,username);
        List<Message> data =new ArrayList<>();

        for(MessageDTO ms:temp){
            data.add(new Message((ms)));
        }

        System.out.println("~~~~~");
        System.out.println(data);
        System.out.println("~~~~~");

        int total = messageMapper.countTotal(username);
        res.put("data",data);
        res.put("total",total);
        return Result.success(res);
    }
}

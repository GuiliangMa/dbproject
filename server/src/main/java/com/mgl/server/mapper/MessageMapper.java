package com.mgl.server.mapper;

import com.mgl.server.controller.dto.MessageDTO;
import com.mgl.server.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper
{
    @Select("select * from message where touser=#{username} order by msgid desc limit #{pageNum},#{pageSize}")
    List<MessageDTO> findPage(Integer pageNum, Integer pageSize, String username);

    @Select("select count(*) from message where touser=#{username}")
    int countTotal(String username);
}

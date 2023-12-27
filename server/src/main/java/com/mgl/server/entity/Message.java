package com.mgl.server.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgl.server.controller.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.annotations.Select;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message
{
    private int msgid;
    private String fromuser;
    private String touser;
    private String msg;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private DateTime sendtime;

    public Message(MessageDTO ms){
        setMsgid(ms.getMsgid());
        setFromuser(ms.getFromuser());
        setTouser(ms.getTouser());
        setMsg(ms.getMsg());
        setSendtime(new DateTime(ms.getSendtime()));
    }
}

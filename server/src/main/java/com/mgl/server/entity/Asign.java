package com.mgl.server.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgl.server.controller.dto.AsignDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Asign
{
    private int asgid;
    private String username;
    private String remark;
    private int capacity;
    private boolean multiMedia;
    private boolean ac;
    private String type;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private DateTime startTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private DateTime endTime;
    private String state;
    private String roomName;

    public Asign(AsignDTO as){
        setAsgid(as.getAsgid());
        setUsername(as.getUsername());
        setRemark(as.getRemark());
        setCapacity(as.getCapacity());
        setMultiMedia(Objects.equals(as.getMultiMedia(), "1"));
        setAc(Objects.equals(as.getAc(), "1"));
        setType(as.getType());
        setStartTime(new DateTime(as.getStartTime()));
        setEndTime(new DateTime(as.getEndTime()));
        setState(as.getState());
        setRoomName(as.getRoomName());
    }
}

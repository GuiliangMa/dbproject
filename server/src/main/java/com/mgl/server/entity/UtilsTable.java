package com.mgl.server.entity;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgl.server.controller.dto.UtilsTableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Calendar;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UtilsTable
{
    private int uid;
    private String roomName;
    private String username;
    private String name;
    private String status;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private DateTime startTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private DateTime endTime;
    private String state;
    private String remark;
    private String props;

    public UtilsTable(UtilsTableDTO utilsTableDTO){
        this.uid=utilsTableDTO.getUid();
        this.roomName=utilsTableDTO.getRoomName();
        this.username=utilsTableDTO.getUsername();
        this.name = utilsTableDTO.getName();
        this.status = utilsTableDTO.getStatus();
        this.startTime = new DateTime(utilsTableDTO.getStartTime());
        this.endTime = new DateTime(utilsTableDTO.getEndTime());
        DateTime now = new DateTime(Calendar.getInstance());
        if(DateUtil.compare(now,this.startTime)<0) setState("待使用");
        if(DateUtil.compare(now,this.startTime)>=0&&DateUtil.compare(now,this.endTime)<=0) setState("在使用");
        if(DateUtil.compare(now,this.endTime)>0) setState("已使用");
        this.remark = utilsTableDTO.getRemark();
        this.props=utilsTableDTO.getProps();
    }
}

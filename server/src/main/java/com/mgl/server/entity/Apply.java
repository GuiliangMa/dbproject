package com.mgl.server.entity;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgl.server.controller.dto.ApplyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Calendar;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Apply
{
    private int apid;
    private int rid;
    private String roomName;
    private String username;
    private String name;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime endTime;
    private String mode;
    private String remark;
    private String msg;
    private String admin;

    public Apply(ApplyDTO applyDTO)
    {
        this.apid = applyDTO.getApid();
        this.rid = applyDTO.getRid();
        this.roomName = applyDTO.getRoomName();
        this.username = applyDTO.getUsername();
        this.name = applyDTO.getName();
        this.status = applyDTO.getStatus();
        this.startTime = new DateTime(applyDTO.getStartTime());
        this.endTime = new DateTime(applyDTO.getEndTime());
        this.mode = applyDTO.getMode();
        this.remark = applyDTO.getRemark();
        this.msg=applyDTO.getMsg();
    }
}

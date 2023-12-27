package com.mgl.server.entity;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgl.server.controller.dto.StudentCourseDTO;
import com.mgl.server.controller.dto.TeachDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Teach
{
    private int tcid;
    private String tid;
    private String name;
    private String cid;
    private String cname;
    private int rid;
    private String roomName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    private int weekday;
    private String strWeekday;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private String beginTime;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private String endTime;
    private int times;
    private String state;
    private String mode;
    private String remark;

    public Teach(TeachDTO dto){
        setTcid(dto.getTcid());
        setTid(dto.getTid());
        setName(dto.getName());
        setCid(dto.getCid());
        setCname(dto.getCname());
        setRid(dto.getRid());
        setRoomName(dto.getRoomName());
        setStartDate(DateUtil.parse(dto.getStartDate()));
        setEndDate(DateUtil.parse(dto.getEndDate()));
        setWeekday(dto.getWeekday());
        setStrWeekday(dto.getStrWeekday());
        setBeginTime(dto.getBeginTime());
        setEndTime(dto.getEndTime());
        setTimes(dto.getTimes());
        setState(dto.getState());
        setMode(dto.getMode());
        setRemark(dto.getRemark());
    }
}

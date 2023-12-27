package com.mgl.server.controller.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AsignDTO
{
    private int asgid;
    private String username;
    private String remark;
    private int capacity;
    private String multiMedia;
    private String ac;
    private String type;
    private String begin;
    private String end;
    private String date;
    private Date startTime;
    private Date endTime;
    private String state;
    private String roomName;
}

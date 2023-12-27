package com.mgl.server.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApplyDTO
{
    private int apid;
    private int rid;
    private String roomName;
    private String username;
    private String name;
    private String status;
    private String begin;
    private String end;
    private String date;
    private Date startTime;
    private Date endTime;
    private String mode;
    private String remark;
    private String msg;
}

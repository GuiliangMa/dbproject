package com.mgl.server.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeachDTO
{
    private int tcid;
    private String tid;
    private String name;
    private String cid;
    private String cname;
    private int rid;
    private String roomName;
    private String startDate;
    private String endDate;
    private int weekday;
    private String strWeekday;
    private String beginTime;
    private String endTime;
    private int times;
    private String state;
    private String mode;
    private String remark;
}

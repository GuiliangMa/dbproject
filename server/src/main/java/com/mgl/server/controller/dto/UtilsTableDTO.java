package com.mgl.server.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UtilsTableDTO
{
    private int uid;
    private String roomName;
    private String username;
    private String name;
    private String status;
    private Date startTime;
    private Date endTime;
    private String state;
    private String remark;
    private String props;
}

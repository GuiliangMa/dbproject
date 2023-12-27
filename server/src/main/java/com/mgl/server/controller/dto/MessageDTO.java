package com.mgl.server.controller.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO
{
    private int msgid;
    private String fromuser;
    private String touser;
    private String msg;
    private String sendtime;
}

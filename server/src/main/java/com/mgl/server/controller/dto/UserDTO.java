package com.mgl.server.controller.dto;

import lombok.Data;
import lombok.ToString;

/*
* 接受前端登录请求的参数
* */
@Data
@ToString
public class UserDTO
{
    private String username;
    private String password;
    private String status;
    private String nickname;
    private String token;
}

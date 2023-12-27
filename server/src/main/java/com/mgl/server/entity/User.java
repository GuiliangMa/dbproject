package com.mgl.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
//lombok 中注解，免去一些列的set和get方法
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    private String username;

    @JsonIgnore
    private String password;
    private String nickname;
    private String status;
    private String email;
    private String phone;
}

//如果用Mybatis-plus，可以用@TableName(value="")来指定具体表名，用@TableId()来指明主键一些操作，用@TableField(value="")来指明属性名
package com.mgl.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Teacher
{
    private String tid;
    private String name;
    private String gender;
    private String username;
    private String email;
    private String phone;
}

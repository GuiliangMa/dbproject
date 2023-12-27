package com.mgl.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Course
{
    private String cid;
    private String cname;
    private double credit;
    private String ctype;
}
